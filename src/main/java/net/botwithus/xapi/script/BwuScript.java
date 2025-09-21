package net.botwithus.xapi.script;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public abstract class BwuScript extends PermissiveScript {
    private BwuGraphicsContext graphicsContext = null;
    public Stopwatch STOPWATCH;

    public LocalPlayer player;
    public BotStat botStatInfo = new BotStat();

    public String getName() {
        return this.getClass().getSimpleName();
    }

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

            Path path = Paths.get(System.getProperty("user.home"), ".botwithus", "configs", getName() + "_settings.json");
            Files.createDirectories(path.getParent());

            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                new GsonBuilder().setPrettyPrinting().create().toJson(obj, writer);
            }
        } catch (Exception e) {
            println("Failed to save persistent data");
            e.printStackTrace();
        }
    }

    public void performLoadPersistentData() {
        try {
            Path path = Paths.get(System.getProperty("user.home"), ".botwithus", "configs", getName() + "_settings.json");
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                    if (obj != null) {
                        loadPersistentData(obj);
                    }
                }
            }
        } catch (Exception e) {
            println("Failed to load persistent data");
            e.printStackTrace();
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
