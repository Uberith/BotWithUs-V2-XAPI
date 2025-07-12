package net.botwithus.xapi.script.permissive.node.leaf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.permissive.node.LeafNode;

public class ChainedActionLeaf extends LeafNode {
    private final List<Action> actions;
    private int currentActionIndex = 0;
    private int currentTicks = 0;
    private boolean validationState = false;
    private boolean hasExpired = false;
    private PermissiveScript script;
    private Callable<Boolean> onSuccess, onFailure;


    public ChainedActionLeaf(PermissiveScript script, Action... actions) {
        super(script);
        this.script = script;
        this.actions = new ArrayList<>(List.of(actions));
    }

    public ChainedActionLeaf(PermissiveScript script, String desc, Action... actions) {
        super(script, desc);
        this.script = script;
        this.actions = new ArrayList<>(List.of(actions));
    }

    @Override
    public void execute() {

        Action currentAction = actions.get(currentActionIndex);

        try {
            if (currentAction.callable.call()) {
                // Action succeeded, move to next action
                currentActionIndex++;
                currentTicks = 0;
                validationState = currentActionIndex == actions.size();

                if (validationState) {
                    // Action succeeded, move to next action
                    currentActionIndex = 0;
                    script.println("[CAL]: \"" + getDesc() + "\" completed successfully");
                    if (onSuccess != null) {
                        try {
                            script.println("Calling onSuccess");
                            onSuccess.call();
                        } catch (Exception e) {
                            script.println("Error calling onSuccess: " + e.getMessage());
                        }
                    }
                }
            } else {
                currentTicks++;
                if (currentTicks >= currentAction.timeoutTicks) {
                    // Action timed out, fail the chain
                    validationState = false;
                    hasExpired = true;
                    currentActionIndex = 0;
                    currentTicks = 0;

                    if (onFailure != null) {
                        script.println("Calling onFailure");
                        onFailure.call();
                    }
                } else {
                    // Still waiting for action to complete
                    validationState = false;
                    hasExpired = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            validationState = false;
            currentActionIndex = 0;
            currentTicks = 0;
            hasExpired = true;
        }
    }

    @Override
    public boolean validate() {
        return validationState;
    }

    public boolean hasExpired() {
        return hasExpired;
    }

    public String getProgress() {
        return String.format("%d/%d", currentActionIndex + 1, actions.size());
    }

    public static class Action {
        private final Callable<Boolean> callable;
        private final int timeoutTicks;

        public Action(Callable<Boolean> callable, int timeoutTicks) {
            this.callable = callable;
            this.timeoutTicks = timeoutTicks;
        }
    }

    public static class Builder {
        private final List<Action> actions = new ArrayList<>();
        private final PermissiveScript script;
        private String description;
        private Callable<Boolean> onSuccess, onFailure;

        public Builder(PermissiveScript script) {
            this.script = script;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder addAction(Callable<Boolean> callable, int timeoutTicks) {
            actions.add(new Action(callable, timeoutTicks));
            return this;
        }

        public Builder onSuccess(Callable<Boolean> onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        public Builder onFailure(Callable<Boolean> onFailure) {
            this.onFailure = onFailure;
            return this;
        }

        public ChainedActionLeaf build() {
            Action[] actionsArray = actions.toArray(new Action[0]);
            var leaf = description != null ?
                new ChainedActionLeaf(script, description, actionsArray) :
                new ChainedActionLeaf(script, actionsArray);
            leaf.onSuccess = onSuccess;
            leaf.onFailure = onFailure;
            return leaf;
        }
    }
}
