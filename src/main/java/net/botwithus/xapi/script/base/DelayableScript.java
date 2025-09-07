package net.botwithus.xapi.script.base;

import net.botwithus.rs3.client.Client;
import net.botwithus.scripts.Script;
import net.botwithus.util.Rand;

import java.util.Arrays;
import java.util.concurrent.Callable;

public abstract class DelayableScript extends Script {

    private Callable<Boolean> delayUntil = null,
            delayWhile = null;
    private int ticksToDelay = -1, previousTick, currentTick;

    @Override
    public void run() {
        try {
            currentTick = Client.getServerTick();
            if (currentTick <= previousTick) {
                return;
            }

            if (delayUntil != null) {
                if (delayUntil.call() || ticksToDelay <= 0) {
                    delayUntil = null;
                    ticksToDelay = 0;
                } else {
                    ticksToDelay--;
                }
            } else if (delayWhile != null) {
                if (!delayWhile.call() || ticksToDelay <= 0) {
                    delayWhile = null;
                    ticksToDelay = 0;
                } else {
                    ticksToDelay--;
                }
            }

            if (ticksToDelay > 0) {
                ticksToDelay--;
            } else {
                doRun();
            }

            previousTick = currentTick;
        } catch (Exception e) {
            println("Exception: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
    }

    public abstract void doRun();

    public void delayUntil(Callable<Boolean> condition, int timeoutTicks) {
        delayUntil = condition;
        ticksToDelay = timeoutTicks;
    }
    public void delayWhile(Callable<Boolean> condition, int timeoutTicks) {
        delayWhile = condition;
        ticksToDelay = timeoutTicks;
    }
    public void delay(int ticks) {
        ticksToDelay = ticks;
    }
    public void delay(int min, int max) {
        ticksToDelay = Rand.nextInt(min, max);
    }
}
