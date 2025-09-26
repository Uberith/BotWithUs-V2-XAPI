package net.botwithus.xapi.query.base;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Simple TTL-based cache support for query results.
 * @param <R> cached result type
 */
public final class QueryCache<R> {

    private Duration ttl;
    private Instant expiresAt = Instant.EPOCH;
    private R cachedValue;

    /**
     * Configures the cache TTL. Passing {@code null} or a non-positive duration disables caching.
     *
     * @param ttl time-to-live for cached results
     */
    public synchronized void configure(Duration ttl) {
        this.ttl = normalize(ttl);
        invalidate();
    }

    /**
     * @return true when caching is enabled
     */
    public synchronized boolean isEnabled() {
        return ttl != null;
    }

    /**
     * Clears any cached value immediately.
     */
    public synchronized void invalidate() {
        cachedValue = null;
        expiresAt = Instant.EPOCH;
    }

    /**
     * Returns the cached value if present and still valid, otherwise computes a fresh value.
     *
     * @param supplier computation used when the cache is empty or expired
     * @return cached or freshly-computed value
     */
    public R getOrCompute(Supplier<R> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        Duration localTtl;
        Instant localExpiry;
        R localValue;
        synchronized (this) {
            localTtl = this.ttl;
            localExpiry = this.expiresAt;
            localValue = this.cachedValue;
        }

        if (localTtl == null) {
            return supplier.get();
        }

        var now = Instant.now();
        if (localValue == null || now.isAfter(localExpiry)) {
            synchronized (this) {
                now = Instant.now();
                if (cachedValue == null || now.isAfter(expiresAt)) {
                    cachedValue = supplier.get();
                    expiresAt = now.plus(localTtl);
                }
                return cachedValue;
            }
        }
        return localValue;
    }

    private static Duration normalize(Duration ttl) {
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            return null;
        }
        return ttl;
    }
}
