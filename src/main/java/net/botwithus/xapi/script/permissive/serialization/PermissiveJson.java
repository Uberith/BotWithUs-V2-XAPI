package net.botwithus.xapi.script.permissive.serialization;

/**
 * JSON representation of a Permissive condition in the PermissiveScript structure
 */
public class PermissiveJson {
    private String name;            // Name of the permissive condition
    private boolean lastResult;     // Last evaluation result
    private String description;     // Description of what this permissive checks

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLastResult() {
        return lastResult;
    }

    public void setLastResult(boolean lastResult) {
        this.lastResult = lastResult;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 