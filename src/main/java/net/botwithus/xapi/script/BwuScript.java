package net.botwithus.xapi.script;

import net.botwithus.scripts.Info;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.ui.BwuGraphicsContext;
import net.botwithus.xapi.util.statistic.BotStat;
import net.botwithus.xapi.util.time.Stopwatch;

import java.util.HashMap;
import java.util.Map;


public abstract class BwuScript extends PermissiveScript {

    private BwuGraphicsContext graphicsContext = null;
    public final Stopwatch STOPWATCH = new Stopwatch();

    private final Info info = getClass().getAnnotation(Info.class);
    public BotStat botStatInfo = new BotStat();

    // Map for states, with a name key for each state
    private final Map<String, State> states = new HashMap<>();
    private State currentState;

    public BwuScript(State... state) {
        this.currentState = state[0];

        for (State s : state) {
            states.put(s.getName(), s);
        }
    }

    @Override
    public Branch getRootNode() {
        return currentState != null ? currentState.getNode() : null;
    }

    @Override
    public void onDraw(Workspace workspace) {
        if (graphicsContext == null) {
            graphicsContext = new BwuGraphicsContext(this, workspace);
        }

        graphicsContext.draw();
    }

    public abstract void onDrawConfig(Workspace workspace);

    public Info getInfo() {
        return info;
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
