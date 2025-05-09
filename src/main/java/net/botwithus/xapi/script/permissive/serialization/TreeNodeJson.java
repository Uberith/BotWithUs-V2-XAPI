package net.botwithus.xapi.script.permissive.serialization;

import java.util.List;

/**
 * JSON representation of a TreeNode in the PermissiveScript structure
 */
public class TreeNodeJson {
    private String type;            // "branch" or "leaf"
    private String nodeClass;       // Actual class name
    private String description;     // Node description
    private String definedIn;       // Where the node was defined
    private TreeNodeJson success;   // Success node (if any)
    private TreeNodeJson failure;   // Failure node (if any)
    private List<InterlockJson> interlocks;  // Interlocks for branches
    private String leafAction;      // Description of leaf node action (if leaf)
    private boolean isCallable;     // Whether success/failure nodes are callables
    private String lastValidateResult; // Result of last validate() call

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNodeClass() {
        return nodeClass;
    }

    public void setNodeClass(String nodeClass) {
        this.nodeClass = nodeClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefinedIn() {
        return definedIn;
    }

    public void setDefinedIn(String definedIn) {
        this.definedIn = definedIn;
    }

    public TreeNodeJson getSuccess() {
        return success;
    }

    public void setSuccess(TreeNodeJson success) {
        this.success = success;
    }

    public TreeNodeJson getFailure() {
        return failure;
    }

    public void setFailure(TreeNodeJson failure) {
        this.failure = failure;
    }

    public List<InterlockJson> getInterlocks() {
        return interlocks;
    }

    public void setInterlocks(List<InterlockJson> interlocks) {
        this.interlocks = interlocks;
    }

    public String getLeafAction() {
        return leafAction;
    }

    public void setLeafAction(String leafAction) {
        this.leafAction = leafAction;
    }

    public boolean isCallable() {
        return isCallable;
    }

    public void setCallable(boolean callable) {
        isCallable = callable;
    }

    public String getLastValidateResult() {
        return lastValidateResult;
    }

    public void setLastValidateResult(String lastValidateResult) {
        this.lastValidateResult = lastValidateResult;
    }
} 