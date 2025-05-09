package net.botwithus.xapi.util.statistic;

import net.botwithus.rs3.stats.Stats;
import net.botwithus.xapi.util.Math;
import net.botwithus.xapi.util.time.DurationStringFormat;
import net.botwithus.xapi.util.time.Stopwatch;
import net.botwithus.xapi.util.StringUtils;
import net.botwithus.xapi.util.collection.PairList;
import net.botwithus.xapi.util.time.Timer;

import java.text.NumberFormat;


public class XPInfo {
    public Stats statType;

    private int startLvl, startXP, currentLvl, currentXP, xpUntilNextLevel;

    public XPInfo(Stats stat) {
        this.statType = stat;
        reset();
    }

    public void update() {
        this.currentLvl = statType.getLevel();
        this.currentXP = statType.getXp();
        this.xpUntilNextLevel = statType.getStat().getXpUntilNextLevel();
    }

    public void reset() {
        this.currentLvl = statType.getLevel();
        this.currentXP = statType.getXp();
        this.xpUntilNextLevel = statType.getStat().getXpUntilNextLevel();
        currentLvl = startLvl;
        currentXP = startXP;
    }

    public int getLevelsGained() {
        return currentLvl - startLvl;
    }

    public int getGainedXP() {
        return currentXP - startXP;
    }

    public int getXPHour(Stopwatch watch) {
        return Math.getUnitsPerHour(watch, getGainedXP());
    }

    public int getSecondsUntilLevel(Stopwatch watch) {
        return (int) ((((double) xpUntilNextLevel) / ((double) getXPHour(watch))) * 3600.0);
    }

    public PairList<String, String> getPairList(Stopwatch stopWatch) {
        PairList<String, String> list = new PairList<>();
        if (currentXP > startXP) {
            var name = StringUtils.toTitleCase(statType.toString());
            list.add(name + " Level: ", currentLvl + " (" + getLevelsGained() + " Gained)");
            list.add(name + " XP Gained: ", NumberFormat.getIntegerInstance().format(getGainedXP()) + " (" + NumberFormat.getIntegerInstance().format(getXPHour(stopWatch)) + "/Hour)");
            list.add(name + " TTL: ", Timer.secondsToFormattedString(getSecondsUntilLevel(stopWatch), DurationStringFormat.DESCRIPTION));
        }
        return list;
    }

    public Stats getSkillsType() {
        return statType;
    }
}