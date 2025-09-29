package net.botwithus.xapi.query.base;

import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.world.Vector3f;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;
import net.botwithus.xapi.query.result.EntityResultSet;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class EntityQuery<T extends Entity> implements Query<T, EntityResultSet<T>> {

    protected Predicate<T> root;

    /**
     * Constructs a new EntityQuery with a default predicate.
     */
    @SuppressWarnings("unused")
    public EntityQuery() {
        root = t -> true;
    }

    /**
     * Hook invoked whenever the predicate chain changes. Subclasses can override to invalidate caches.
     */
    protected void predicateChanged() {
        // default no-op
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q type(EntityType... entityType) {
        if (entityType.length == 0) {
            return (Q) this;
        }
        this.root = this.root.and(t -> Arrays.stream(entityType).anyMatch(i -> t.getType() == i));
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q coordinate(Coordinate... coordinate) {
        if (coordinate.length == 0) {
            return (Q) this;
        }
        this.root = this.root.and(t -> Arrays.stream(coordinate).anyMatch(i -> t.getCoordinate().equals(i)));
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q direction(Vector3f... vector3f) {
        if (vector3f.length == 0) {
            return (Q) this;
        }
        this.root = this.root.and(t -> Arrays.stream(vector3f).anyMatch(i -> t.getDirection().equals(i)));
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q valid(boolean valid) {
        this.root = this.root.and(t -> t.isValid() == valid);
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q inside(Area area) {
        this.root = this.root.and(t -> area.contains(t.getCoordinate()));
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q outside(Area area) {
        this.root = this.root.and(t -> !area.contains(t.getCoordinate()));
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q distance(double distance) {
        this.root = this.root.and(t -> Distance.to(t) <= distance);
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q and(EntityQuery<T> other) {
        this.root = this.root.and(other.root);
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q or(EntityQuery<T> other) {
        this.root = this.root.or(other.root);
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q inverse() {
        this.root = this.root.negate();
        predicateChanged();
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q mark() {
        return (Q) this;
    }
}
