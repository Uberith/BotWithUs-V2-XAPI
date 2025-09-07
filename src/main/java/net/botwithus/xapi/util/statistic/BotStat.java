package net.botwithus.xapi.util.statistic;

import java.util.LinkedHashMap;

public class BotStat {

    public LinkedHashMap<Integer, XPInfo> xpInfoMap = new LinkedHashMap<>();
    public LinkedHashMap<String, String> displayInfoMap = new LinkedHashMap<>();

    public BotStat() {
    }

    public void resetStats() {
        xpInfoMap.forEach((k, v) -> v.reset());
    }

}