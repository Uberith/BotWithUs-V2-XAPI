package net.botwithus.xapi.script.base;

import net.botwithus.scripts.Script;
import net.botwithus.util.Rand;

import java.util.concurrent.Callable;
import java.util.logging.Level;

public abstract class DelayableScript extends Script {

    private Callable<Boolean> delayUntil = null,
            delayWhile = null;
    private int delay = -1;

    @Override
    public void run() {
        try {
            if (delayUntil != null) {
                if (delayUntil.call() || delay <= 0) {
                    delayUntil = null;
                } else {
                    delay--;
                }
            } else if (delayWhile != null) {
                if (!delayWhile.call() || delay <= 0) {
                    delayWhile = null;
                } else {
                    delay--;
                }
            } else if (delay > 0) {
                delay--;
            } else {
                doRun();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void doRun();

    public void delayUntil(Callable<Boolean> condition, int timeoutTicks) {
        delayUntil = condition;
        delay = timeoutTicks;
    }
    public void delayWhile(Callable<Boolean> condition, int timeoutTicks) {
        delayWhile = condition;
        delay = timeoutTicks;
    }
    public void delay(int ticks) {
        delay = ticks;
    }
    public void delay(int min, int max) {
        delay = Rand.nextInt(min, max);
    }
}
