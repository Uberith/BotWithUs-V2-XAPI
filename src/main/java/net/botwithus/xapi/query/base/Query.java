package net.botwithus.xapi.query.base;

import net.botwithus.xapi.query.result.ResultSet;

import java.util.function.Predicate;

public interface Query <T, R extends ResultSet<T>> extends Iterable<T>, Predicate<T> {
    R results();
}
