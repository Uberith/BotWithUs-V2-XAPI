package net.botwithus.xapi.script;

import com.google.gson.JsonObject;

import net.botwithus.events.EventInfo;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.inventories.events.InventoryEvent;
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
    public Stopwatch STOPWATCH;

    public LocalPlayer player;
    public BotStat botStatInfo = new BotStat();

    @Override
    public void onDraw(Workspace workspace) {
        super.onDraw(workspace);
        if (graphicsContext == null) {
            graphicsContext = new BwuGraphicsContext(this, workspace);
        }

        graphicsContext.draw();
    }

    public abstract void onDrawConfig(Workspace workspace);

    @Override
    public void onInitialize() {
        super.onInitialize();
        try {
            performLoadPersistentData();
        } catch (Exception e) {
            println("Failed to load persistent data");
        }
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

    @Override
    public boolean onPreTick() {
        player = LocalPlayer.self();
        return super.onPreTick() && player != null && player.isValid();
    }

    @Override
    public void onActivation() {
        super.onActivation();
        if (STOPWATCH == null) {
            STOPWATCH = Stopwatch.startNew();
        } else {
            STOPWATCH.resume();
        }
    }

    @Override
    public void onDeactivation() {
        super.onDeactivation();
        STOPWATCH.pause();
    }

    @EventInfo(type = InventoryEvent.class)
    private void onInventoryEvent(InventoryEvent event) {

        // New Item Acquired
        if (event.oldItem().getId() <= -1 && event.newItem().getId() > -1) {
            onItemAcquired(event);
        } else if (event.oldItem().getId() > -1 && event.newItem().getId() <= -1) {
            onItemRemoved(event);
        } else {
            onItemChange(event);
        }
    }

    protected void onItemAcquired(InventoryEvent event) {};
    protected void onItemRemoved(InventoryEvent event) {};
    protected void onItemChange(InventoryEvent event) {};
}
