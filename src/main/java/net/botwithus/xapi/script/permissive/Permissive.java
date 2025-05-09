package net.botwithus.xapi.script.permissive;

import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.serialization.PermissiveJson;

import java.util.function.Supplier;

public class Permissive implements Supplier<Boolean> {
    private final String name;
    private final Supplier<Boolean> predicate;
    private EvaluationResult<Boolean> lastResult = new EvaluationResult<>(false);

    public Permissive(String name, Supplier<Boolean> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    @Override
    public Boolean get() {
        try {
            boolean result = predicate.get();
//            script.getLogger().info("[" + Thread.currentThread().getName() + "]: " + "[Permissive] " + name + ": " + result);
            lastResult = new EvaluationResult<>(result);
            return result;
        } catch (Exception e) {
//            script.getLogger().severe(e.getMessage() + "\nException thrown in permissive predicate: " + name);
            lastResult = new EvaluationResult<>(false);
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public EvaluationResult<Boolean> getLastResult() {
        return lastResult;
    }

    /**
     * Converts this Permissive to its JSON representation
     * @return JSON representation of this Permissive
     */
    public PermissiveJson toJson() {
        PermissiveJson json = new PermissiveJson();
        json.setName(name);
        json.setLastResult(lastResult.getResult());
        return json;
    }
}
