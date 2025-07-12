package net.botwithus.xapi.script.permissive.base;

import java.util.Map;

import net.botwithus.xapi.script.base.DelayableScript;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.node.TreeNode;
import net.botwithus.xapi.script.permissive.node.leaf.ChainedActionLeaf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public abstract class PermissiveScript extends DelayableScript {
    private boolean debugMode = false;
    private State currentState;
    private long runtimeTickCount = 0;

    // Map for states, with a name key for each state
    private final Map<String, State> states = new HashMap<>();

    private ChainedActionLeaf activeChainedAction = null;

    /***
     * Main game tick logic
     */
    @Override
    public void doRun() {
        println("Processing game tick");
        runtimeTickCount++;

        if (!onPreTick()) {
            println("Pre-tick failed, skipping main tick logic");
            return;
        }

        if (currentState != null) {
            println("Current state: " + currentState.getName());
        } else {
            println("No current state");
        }

        // If we have an active chained action, continue executing it
        if (activeChainedAction != null) {
            try {
                println("Executing chained action: " + activeChainedAction.getDesc() + "(" + activeChainedAction.getProgress() + ")");
                activeChainedAction.execute();
                if (activeChainedAction.validate()) {
                    // Chain completed successfully
                    println("Active chained action completed successfully");
                    activeChainedAction = null;
                } else if (activeChainedAction.hasExpired()) {
                    // Chain failed, reset it
                    println("Active chained action failed, resetting");
                    activeChainedAction = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                println("Active chained action failed, aborting: " + e.getMessage());
                activeChainedAction = null;
            }
        } else {
            try {
                traverseAndExecute(getRootNode());
            } catch (Exception e) {
                e.printStackTrace();
                println("Root task traversal failed: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
    }

    /**
     * Traverses the tree, executes all nodes, and checks for ChainedActionLeaf nodes.
     * If a ChainedActionLeaf is found and not validated, it becomes the active chained action.
     */
    private void traverseAndExecute(TreeNode node) {
        if (node == null) {
            println("Node is null, skipping tree traversal");
            return;
        }


        // Continue traversal if not a leaf node
        if (!node.isLeaf()) {
            if (node.validate()) {
                println("[Node] \"" + node.getDesc() + "\" SUCCESS -> " + node.successNode().getDesc());
                traverseAndExecute(node.successNode());
            } else {
                println("[Node] \"" + node.getDesc() + "\" NOT_MET -> " + node.failureNode().getDesc());
                traverseAndExecute(node.failureNode());
            }
        } else { // Execute the leaf node
            try {
                // Check if it's a ChainedActionLeaf that needs to become active
                if (node instanceof ChainedActionLeaf chainedAction) {
                    println("Chained action found, setting as active: " + chainedAction.getDesc());
                    activeChainedAction = chainedAction;
                    return;
                } else {
                    println("Executing leaf node: " + node.getDesc());
                    node.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
                println("Leaf node failed: " + e.getMessage());
            }
        }
    }

    public Branch getRootNode() {
        return currentState != null ? currentState.getNode() : null;
    }

    public long getRuntimeTickCount() {
        return runtimeTickCount;
    }

    /**
     * Initialize the script with the given states.
     * @param state The states to initialize the script with.
     */
    public void initStates(State... state){
        this.currentState = state[0];
        for (State s : state) {
            states.put(s.getName(), s);
        }
    }

    /**
     * Get the state with the given name.
     * @param name The name of the state to get.
     * @return The state with the given name.
     */
    public State getState(String name){
        return states.get(name);
    }

    /***
     * Code that runs before the main game tick logic
     */
    public boolean onPreTick() {
        return true;
    }

    public void debug(String message) {
        if (debugMode) {
            println("[Debug] " + message);
        }
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }


    public State getCurrentState() {
        return currentState;
    }

    // Methods for managing states
    public void addState(State... state) {
        for (State s : state) {
            states.put(s.getName(), s);
        }
    }

    public void setCurrentState(String name) {
        currentState = states.get(name);
    }

    public String getStatus() {
        return currentState != null ? currentState.getStatus() : null;
    }

    public boolean setStatus(String status) {
        if (currentState != null) {
            println("[Status] " + status);
            currentState.setStatus(status);
            return true;
        }
        return false;
    }

    public static class State {
        private String name, status;
        private Branch node;

        public State(String name) {
            this.name = name;
        }

        public State(String name, Branch node) {
            this.name = name;
            this.node = node;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Branch getNode() {
            return node;
        }

        public void setNode(Branch node) {
            this.node = node;
        }
    }
}
