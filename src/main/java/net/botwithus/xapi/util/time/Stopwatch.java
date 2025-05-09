package net.botwithus.xapi.util.time;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Stopwatch {
    private Instant start;
    private final List<Instant> breakpoints = new ArrayList<>();

    public Stopwatch() {
    }

    public static Stopwatch startNew() {
        Stopwatch s = new Stopwatch();
        s.start();
        return s;
    }

    public void start() {
        this.start = Instant.now();
    }

    public long elapsed() {
        if (start == null) {
            return 0;
        }
        Instant now = Instant.now();
        Duration pausedDuration = Duration.ZERO;
        for (int i = 0; i < breakpoints.size(); i += 2) {
            Instant pauseStart = breakpoints.get(i);
            Instant pauseEnd = (i + 1 < breakpoints.size()) ? breakpoints.get(i + 1) : now;
            pausedDuration = pausedDuration.plus(Duration.between(pauseStart, pauseEnd));
        }
        return Duration.between(start, now).minus(pausedDuration).toMillis();
    }

    public void pause() {
        breakpoints.add(Instant.now());
    }

    public void resume() {
        // Resume only if the last pause hasn't been ended (i.e., if the size is odd)
        if (breakpoints.size() % 2 != 0) {
            breakpoints.add(Instant.now());
        }
    }
}