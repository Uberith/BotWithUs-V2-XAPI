package net.botwithus.xapi.util;

import net.botwithus.xapi.util.time.Stopwatch;

import java.util.concurrent.TimeUnit;

public class BwuMath {
    public static int getUnitsPerHour(Stopwatch stopwatch, int unitCount) {
        if (unitCount == 0) {
            return 0;
        } else {
            double seconds = (double) stopwatch.elapsed() / 1000;
            double hours = seconds / 3600.0;
            double itemPerHour = (double) unitCount / hours;
            return (int) itemPerHour;
        }
    }

    public static double rate(TimeUnit unit, long runtime, double value) {
        if (unit != null && runtime > 0L && value != 0.0D) {
            double nano = 1.0E-7D;
            double micro = nano * 10000.0D;
            double millis = micro * 1000.0D;
            double second = millis * 1000.0D;
            double minute = second * 60.0D;
            double hour = minute * 60.0D;
            double day = hour * 24.0D;
            double[] measures = new double[]{nano, micro, millis, second, minute, hour, day};
            return value * measures[unit.ordinal()] / (double) runtime;
        } else {
            return 0.0D;
        }
    }

}