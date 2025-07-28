package net.botwithus.xapi.script.permissive.base;

import java.util.Map;

import net.botwithus.xapi.script.base.DelayableScript;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.node.TreeNode;
import net.botwithus.xapi.script.permissive.node.leaf.ChainedActionLeaf;
import net.botwithus.xapi.util.Logger;

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
    
    // Logger instance for this script
    private final Logger logger = Logger.getLogger(this.getClass());

    /***
     * Main game tick logic
     */
    @Override
    public void doRun() {
        logger.debug("Processing game tick");
        runtimeTickCount++;

        if (!onPreTick()) {
            logger.warn("Pre-tick failed, skipping main tick logic");
            return;
        }

        if (currentState != null) {
            logger.debug("Current state: " + currentState.getName());
        } else {
            logger.warn("No current state");
        }

        // If we have an active chained action, continue executing it
        if (activeChainedAction != null) {
            try {
                logger.debug("Executing chained action: " + activeChainedAction.getDesc() + "(" + activeChainedAction.getProgress() + ")");
                activeChainedAction.execute();
                if (activeChainedAction.validate()) {
                    // Chain completed successfully
                    logger.info("Active chained action completed successfully");
                    activeChainedAction = null;
                } else if (activeChainedAction.hasExpired()) {
                    // Chain failed, reset it
                    logger.warn("Active chained action failed, resetting");
                    activeChainedAction = null;
                }
            } catch (Exception e) {
                logger.error("Active chained action failed, aborting: " + e.getMessage(), e);
                activeChainedAction = null;
            }
        } else {
            try {
                traverseAndExecute(getRootNode());
            } catch (Exception e) {
                logger.error("Root task traversal failed: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Traverses the tree, executes all nodes, and checks for ChainedActionLeaf nodes.
     * If a ChainedActionLeaf is found and not validated, it becomes the active chained action.
     */
    private void traverseAndExecute(TreeNode node) {
        if (node == null) {
            logger.warn("Node is null, skipping tree traversal");
            return;
        }


        // Continue traversal if not a leaf node
        if (!node.isLeaf()) {
            if (node.validate()) {
                logger.debug("[Node] \"" + node.getDesc() + "\" SUCCESS -> " + node.successNode().getDesc());
                traverseAndExecute(node.successNode());
            } else {
                logger.debug("[Node] \"" + node.getDesc() + "\" NOT_MET -> " + node.failureNode().getDesc());
                traverseAndExecute(node.failureNode());
            }
        } else { // Execute the leaf node
            try {
                // Check if it's a ChainedActionLeaf that needs to become active
                if (node instanceof ChainedActionLeaf chainedAction) {
                    logger.info("Chained action found, setting as active: " + chainedAction.getDesc());
                    activeChainedAction = chainedAction;
                    return;
                } else {
                    logger.debug("Executing leaf node: " + node.getDesc());
                    node.execute();
                }
            } catch (Exception e) {
                logger.error("Leaf node failed: " + e.getMessage(), e);
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
    public void initStates(State... state) {
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
            logger.debug("[Debug] " + message);
        }
    }
    
    /**
     * Get the logger instance for this script
     * @return The logger instance
     */
    public Logger getLogger() {
        return logger;
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
            logger.info("[Status] " + status);
            currentState.setStatus(status);
            return true;
        }
        return false;
    }

    public static abstract class State {
        private String name, status;
        private Branch node;

        public State(String name) {
            this.name = name;
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

        public abstract void initializeNodes();
    }
}
