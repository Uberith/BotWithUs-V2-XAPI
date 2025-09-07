package net.botwithus.xapi.script.permissive.node;

import net.botwithus.scripts.Script;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;

import java.util.concurrent.Callable;

public class LeafNode extends TreeNode {
    private Callable<Boolean> execute = null;
    private Runnable runnable = null;
    private boolean validate = false;

    public LeafNode(PermissiveScript script) {
        super(script);
    }

    public LeafNode(PermissiveScript script, String desc) {
        super(script, desc);
    }

    public LeafNode(PermissiveScript script, String desc, String definedIn) {
        super(script, desc, definedIn);
    }

    public LeafNode(PermissiveScript script, Callable<Boolean> execute) {
        super(script);
        this.execute = execute;
    }

    public LeafNode(PermissiveScript script, String desc, Callable<Boolean> execute) {
        super(script, desc);
        this.execute = execute;
    }

    public LeafNode(PermissiveScript script, String desc, String definedIn, Callable<Boolean> execute) {
        super(script, desc, definedIn);
        this.execute = execute;
    }

    public LeafNode(PermissiveScript script, Runnable runnable) {
        super(script);
        this.runnable = runnable;
    }

    public LeafNode(PermissiveScript script, String desc, Runnable runnable) {
        super(script, desc);
        this.runnable = runnable;
    }

    public LeafNode(PermissiveScript script, String desc, String definedIn, Runnable runnable) {
        super(script, desc, definedIn);
        this.runnable = runnable;
    }

    @Override
    public void execute() {
        try {
            if (execute != null) {
                validate = execute.call();
            } else if (runnable != null) {
                runnable.run();
                validate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TreeNode successNode() {
        return null;
    }

    @Override
    public boolean validate() {
        script.info("    [Leaf] " + getDesc() + ": " + validate);
        return validate;
    }

    @Override
    public TreeNode failureNode() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * Clones this LeafNode with a new description.
     *
     * @param description The new description for the cloned node.
     * @return A new LeafNode instance with the same properties but a different description.
     */
    public LeafNode clone(String description) {
        return new LeafNode(script, description, getDefinedIn(), runnable);
    }
}
