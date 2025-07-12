package net.botwithus.xapi.script.permissive.node;

import net.botwithus.scripts.Script;
import net.botwithus.xapi.script.permissive.EvaluationResult;
import net.botwithus.xapi.script.permissive.interfaces.ITreeNode;
import net.botwithus.xapi.script.permissive.serialization.TreeNodeJson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class TreeNode implements ITreeNode {
    private EvaluationResult<Boolean> latestValidate = new EvaluationResult<>(false);
    private String definedIn = "";

    protected Script script;
    private Callable<String> desc = () -> "";

    public TreeNode(Script script) {
        this.script = script;
    }

    public TreeNode(Script script, String desc) {
        this.script = script;
        this.desc = () -> desc;
    }

    public TreeNode(Script script, String desc, String definedIn) {
        this.script = script;
        this.desc = () -> desc;
        this.definedIn = definedIn;
    }

    /** {@inheritDoc} */
    @Override
    public abstract void execute() throws Exception;

    /** {@inheritDoc} */
    @Override
    public abstract TreeNode successNode();

    /** {@inheritDoc} */
    @Override
    public abstract boolean validate();

    /** {@inheritDoc} */
    @Override
    public abstract TreeNode failureNode();

    /** {@inheritDoc} */
    @Override
    public abstract boolean isLeaf();

    /**
     * Serializes this node and its children into a JSON representation
     * @return JSON representation of this node and its subtree
     */
    public TreeNodeJson toJson() {
        TreeNodeJson json = new TreeNodeJson();
        json.setType(isLeaf() ? "leaf" : "branch");
        json.setNodeClass(getClass().getSimpleName());
        json.setDescription(getDesc());
        json.setDefinedIn(getDefinedIn());
        json.setLastValidateResult(String.valueOf(latestValidate.getResult()));

        // Handle success and failure nodes if they exist
        TreeNode success = successNode();
        if (success != null) {
            json.setSuccess(success.toJson());
        }

        TreeNode failure = failureNode();
        if (failure != null) {
            json.setFailure(failure.toJson());
        }

        return json;
    }

    public String getDesc() {
        if (desc != null) {
            try {
                return desc.call();
            } catch (Exception e){
//                script.getLogger().severe("Failed to determine the result of the Callable<successNodeC>");
//                script.getLogger().throwing(getClass().getName(), "getDesc", e);
            }
        }
        return "";
    }

    public void setDesc(String desc) {
        this.desc = () -> desc;
    }

    public Script getScript() {
        return script;
    }

    public String getDefinedIn() {
        return definedIn;
    }

    /**
     * Traverses the tree from the current node.
     */
    public void traverse() {
        var validate = this.validate();
        this.setLatestValidate(validate);
        if (!this.isLeaf()) {
            var msg = "[TreeNodeBranch] " + (!this.getDesc().isEmpty() ? this.getDesc() : this.getClass().getSimpleName()) + ": " + validate;
//            script.getLogger().info(msg);
            script.println(msg);
            if (validate) {
                this.successNode().traverse();
            } else {
                this.failureNode().traverse();
            }
        } else {
            try {
                this.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EvaluationResult<Boolean> getLatestValidate() {
        return latestValidate;
    }

    public void setLatestValidate(boolean lastLoopValidate) {
        this.latestValidate = new EvaluationResult<>(lastLoopValidate, 1200);
    }

    /**
     * Collects descriptions from all nodes in the tree starting from the specified root node.
     *
     * @param rootNode The root node of the tree to start collecting descriptions from.
     * @return A list of descriptions for all nodes in the tree.
     */
    public static List<String> getAllNodeDescriptions(TreeNode rootNode) {
        List<String> descriptions = new ArrayList<>();
        collectDescriptions(rootNode, descriptions);
        return descriptions;
    }

    /**
     * Helper method to recursively collect descriptions of nodes into the provided list.
     *
     * @param node The current node to collect the description from.
     * @param descriptions The list where descriptions are being collected.
     */
    private static void collectDescriptions(TreeNode node, List<String> descriptions) {
        if (node == null) {
            return; // End of branch
        }

        // Add the current node's description
        descriptions.add(node.getDesc());

        // Recursively collect descriptions from success and failure branches if not a leaf
        if (!node.isLeaf()) {
            collectDescriptions(node.successNode(), descriptions);
            collectDescriptions(node.failureNode(), descriptions);
        }
    }

    /**
     * Adds a flag to the description of all nodes in the tree starting from the specified root node.
     *
     * @param flag The flag to add to the description of all nodes in the tree.
     */
    public <T> void updateDescriptionFlag(Class<T> classType, String flag) {
        updateDescriptionFlag(classType, this, flag);
    }

    /**
     * Adds a flag to the description of all nodes in the tree starting from the specified root node.
     *
     * @param node The root node of the tree to start adding the flag to.
     * @param flag The flag to add to the description of all nodes in the tree.
     */
    public static <T> TreeNode updateDescriptionFlag(Class<T> type, TreeNode node, String flag) {
        if (node == null) {
            return null; // End of branch
        }

        // Add the current node's description
        if (node.getDesc() != null && !node.getDesc().contains(flag)
                && node.getDefinedIn() != null && node.getDefinedIn().contains(type.getSimpleName())) {
            var desc = node.getDesc();
            if (desc.contains("[")) {
                desc = desc.substring(0, desc.indexOf("[") - 1);
            }
            node.setDesc(desc + " " + flag);
        }

        // Recursively collect descriptions from success and failure branches if not a leaf
        if (!node.isLeaf()) {
            updateDescriptionFlag(type, node.successNode(), flag);
            updateDescriptionFlag(type, node.failureNode(), flag);
        }
        return node;
    }

    /**
     * Returns the node that matches the specified description.
     *
     * @param description The description to match.
     */
    public TreeNode findNodeByDescription(String description) {
        if (description == null || description.isEmpty()) {
            return null;
        }

        if (this.getDesc().contains(description)) {
            return this;
        }
        var checkSuccess = this.successNode().findNodeByDescription(description);
        if (checkSuccess != null) {
            return checkSuccess;
        }
        var checkFailure = this.failureNode().findNodeByDescription(description);
        if (checkFailure != null) {
            return checkFailure;
        }
        return null;
    }

}
