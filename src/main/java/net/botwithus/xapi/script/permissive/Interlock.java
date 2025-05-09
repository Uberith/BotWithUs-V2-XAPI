package net.botwithus.xapi.script.permissive;

import net.botwithus.xapi.script.permissive.serialization.InterlockJson;
import net.botwithus.xapi.script.permissive.serialization.PermissiveJson;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interlock {
    private String name;
    private Permissive[] permissives;
    private EvaluationResult<Boolean> status = new EvaluationResult<>(false);

    private Permissive firstOut = null;

    public Interlock(String name, Permissive... permissives) {
        this.name = name;
        this.permissives = permissives;
    }

    public boolean isActive() {
        for (Permissive permissive : permissives) {
            if (!permissive.get()) {
                firstOut = permissive;
                return false;
            }
        }
        firstOut = null;
        return true;
    }

    public String getName() {
        return name;
    }

    public ResultType getResultType() {
        return status.getResultType();
    }

    public void extend(Permissive... permissives) {
        var newPermissives = Arrays.copyOf(this.permissives, this.permissives.length + permissives.length);
        System.arraycopy(permissives, 0, newPermissives, this.permissives.length, permissives.length);
    }

    public Permissive getFirstOut() {
        return firstOut;
    }

    /**
     * Gets all permissives in this interlock
     * @return Array of permissives
     */
    public Permissive[] getPermissives() {
        return permissives;
    }

    /**
     * Converts this Interlock to its JSON representation
     * @return JSON representation of this Interlock
     */
    public InterlockJson toJson() {
        InterlockJson json = new InterlockJson();
        json.setName(name);
        json.setActive(isActive());
        
        // Convert permissives to JSON
        List<PermissiveJson> permissiveJsons = Arrays.stream(permissives)
            .map(Permissive::toJson)
            .collect(Collectors.toList());
        json.setPermissives(permissiveJsons);

        // Set first failed permissive if any
        if (firstOut != null) {
            json.setFirstFailedPermissive(firstOut.getName());
        }

        return json;
    }
}
