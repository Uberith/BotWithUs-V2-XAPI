package net.botwithus.xapi.script.permissive.serialization;

import java.util.List;

/**
 * JSON representation of an Interlock in the PermissiveScript structure
 */
public class InterlockJson {
    private String name;                    // Name of the interlock
    private List<PermissiveJson> permissives; // List of permissive conditions
    private boolean isActive;               // Current active state
    private String firstFailedPermissive;   // Name of first failed permissive (if any)

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PermissiveJson> getPermissives() {
        return permissives;
    }

    public void setPermissives(List<PermissiveJson> permissives) {
        this.permissives = permissives;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFirstFailedPermissive() {
        return firstFailedPermissive;
    }

    public void setFirstFailedPermissive(String firstFailedPermissive) {
        this.firstFailedPermissive = firstFailedPermissive;
    }
} 