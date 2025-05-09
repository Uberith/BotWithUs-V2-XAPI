package net.botwithus.xapi.script.permissive.base;

import net.botwithus.xapi.script.base.DelayableScript;
import net.botwithus.xapi.script.permissive.node.Branch;


public abstract class PermissiveScript extends DelayableScript {

    private Branch rootTask;

    /***
     * Main game tick logic
     */
    @Override
    public void doRun() {
        if (!onPreTick()) {
            return;
        }

        if (rootTask != null) {
            rootTask.traverse();
        } else {
            rootTask = getRootNode();
        }
    }

    public abstract Branch getRootNode();

    /***
     * Code that runs before the main game tick logic
     */
    public boolean onPreTick() {
        return true;
    }
}
