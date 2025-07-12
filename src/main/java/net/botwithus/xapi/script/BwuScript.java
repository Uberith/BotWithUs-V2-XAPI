package net.botwithus.xapi.script;

import com.google.gson.JsonObject;

import net.botwithus.scripts.Info;
import net.botwithus.ui.workspace.ExtInfo;
import net.botwithus.ui.workspace.Workspace;
import net.botwithus.ui.workspace.WorkspaceExtension;
import net.botwithus.xapi.script.permissive.node.Branch;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import net.botwithus.xapi.script.ui.BwuGraphicsContext;
import net.botwithus.xapi.script.ui.interfaces.BuildableUI;
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

    

    @Override
    public void onInitialize() {
        try {
            performLoadPersistentData();
        } catch (Exception e) {
            println("Failed to load persistent data");
        }
    }

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

    public void performSavePersistentData() {
        try {
            JsonObject obj = new JsonObject();

            savePersistentData(obj);
            println("Settings: " + obj);

            // configuration.addProperty(getName() + "|Settings", obj.toString());
        } catch (Exception e) {
            println("Failed to save persistent data");
        }
    }

    public void performLoadPersistentData() {
        try {
            // var settingKey = getName() + "|Settings";
            // if (getConfiguration().getProperty(settingKey) != null && !getConfiguration().getProperty(settingKey).equals("null")) {
            //     var obj = gson.fromJson(getConfiguration().getProperty(settingKey), JsonObject.class);
            //     loadPersistentData(obj);
            // }
        } catch (Exception e) {
            println("Failed to load persistent data");
        }
    }

    public abstract BuildableUI getBuildableUI();
    public abstract void savePersistentData(JsonObject obj);
    public abstract void loadPersistentData(JsonObject obj);
    public abstract String getVersion();
}
