package net.botwithus.xapi.util.time;

import lombok.Getter;
import lombok.Setter;
import net.botwithus.util.Rand;
import net.botwithus.xapi.util.Math;

public class Timer {
    private final Stopwatch stopWatch = new Stopwatch();
    @Setter
    @Getter
    private long minTime, maxTime;
    private boolean hasStarted = false, forceExpired = false;
    private long timerDuration;

    public Timer(long min, long max) {
        minTime = min;
        maxTime = max;
        timerDuration = Rand.nextLong(min, max);
        forceExpired = false;
    }

    public long getRemainingTime() {
        //Debug.log("TimerDuration: " + timerDuration + " | ElapsedTime: " + stopWatch.elapsed());
        return timerDuration - stopWatch.elapsed();
    }

    public long getElapsed() {
        return stopWatch.elapsed();
    }

    public long getRemainingTimeInSeconds() {
        return getRemainingTime() / 1000;
    }

    public long getRemainingTimeInMinutes() {
        return getRemainingTime() / 60000;
    }

    public boolean hasExpired() {
        return forceExpired || getRemainingTime() <= 0;
    }

    public void setExpired() {
        hasStarted = false;
        forceExpired = true;
    }

    public void setRemainingTime(long time) {
        timerDuration = time;
        hasStarted = true;
        stopWatch.start();
    }

    public void reset() {
        forceExpired = false;
        hasStarted = true;
        timerDuration = Rand.nextLong(minTime, maxTime);
        stopWatch.start();
    }

    public void restartSameTimer() {
        forceExpired = false;
        hasStarted = true;
        stopWatch.start();
    }

    public void start() {
        forceExpired = false;
        hasStarted = true;
        stopWatch.start();
    }

    public void stop() {
        hasStarted = false;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public static String secondsToFormattedString(long timeInSeconds, DurationStringFormat stringFormat) {
        long days = timeInSeconds / 86400, hours = (timeInSeconds % 86400) / 3600, minutes = ((timeInSeconds % 86400) % 3600) / 60, seconds = timeInSeconds % 60;

        if (stringFormat == DurationStringFormat.CLOCK) {
            if (timeInSeconds > 86400) {
                return getClockFormat(days) + ":" + getClockFormat(hours) + ":" + getClockFormat(minutes) + ":" + getClockFormat(seconds);
            } else if (timeInSeconds > 3600) {
                return getClockFormat(hours) + ":" + getClockFormat(minutes) + ":" + getClockFormat(seconds);
            } else {
                return getClockFormat(minutes) + ":" + getClockFormat(seconds);
            }
        } else {
            if (timeInSeconds > 86400) {
                return getClockFormat(days) + " day(s) " + getClockFormat(hours) + " hr(s) " + getClockFormat(
                        minutes) + " min(s) " + getClockFormat(seconds) + " sec(s)";
            } else if (timeInSeconds > 3600) {
                return getClockFormat(hours) + " hr(s) " + getClockFormat(minutes) + " min(s) " + getClockFormat(seconds) + " sec(s)";
            } else {
                return getClockFormat(minutes) + " min(s) " + getClockFormat(seconds) + " sec(s)";
            }
        }
    }

    private static String getClockFormat(long number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return Long.toString(number);
        }
    }
}