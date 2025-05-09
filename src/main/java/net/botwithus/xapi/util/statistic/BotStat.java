package net.botwithus.xapi.util.statistic;

import java.util.LinkedHashMap;

public class BotStat {

    public LinkedHashMap<Integer, XPInfo> xpInfoMap = new LinkedHashMap<>();
    public LinkedHashMap<String, String> displayInfoMap = new LinkedHashMap<>();

    public String runTime, currentTask;

    public BotStat() {
        runTime = "";
        currentTask = "";
    }

    public void setValues(String runTime, String currentTask) {
        this.runTime = runTime;
        this.currentTask = currentTask;
    }

    public void resetStats() {
        xpInfoMap.forEach((k, v) -> v.reset());
    }
}