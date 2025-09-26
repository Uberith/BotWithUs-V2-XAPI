package net.botwithus.xapi.query.base;

import net.botwithus.xapi.query.result.ResultSet;
import net.botwithus.xapi.script.permissive.Permissive;

import java.util.Objects;
import java.util.function.Predicate;

public interface Query<T, R extends ResultSet<T>> extends Iterable<T>, Predicate<T> {
    R results();

    /**
     * Bridges this query into a permissive predicate that succeeds when the query yields results.
     *
     * @param name human-readable identifier for the permissive
     * @return permissive evaluating this query each tick
     */
    default Permissive asPermissive(String name) {
        return asPermissive(name, resultSet -> !resultSet.isEmpty());
    }

    /**
     * Bridges this query into a permissive using the provided condition.
     *
     * @param name human-readable identifier for the permissive
     * @param satisfied predicate evaluated against the latest query results
     * @return permissive evaluating this query each tick
     */
    default Permissive asPermissive(String name, Predicate<R> satisfied) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(satisfied, "satisfied");
        return new Permissive(name, () -> satisfied.test(results()));
    }
}
