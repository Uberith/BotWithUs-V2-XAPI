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
     * Filters entities by type.
     *
     * @param entityType the entity types to filter by
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q type(EntityType... entityType) {
        this.root = this.root.and(t -> Arrays.stream(entityType).anyMatch(i -> t.getType() == i));
        return (Q) this;
    }

    /**
     * Filters entities by coordinate.
     *
     * @param coordinate the coordinates to filter by
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q coordinate(Coordinate... coordinate) {
        this.root = this.root.and(t -> Arrays.stream(coordinate).anyMatch(i -> t.getCoordinate().equals(i)));
        return (Q) this;
    }

    /**
     * Filters entities by direction.
     *
     * @param vector3f the directions to filter by
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q direction(Vector3f... vector3f) {
        this.root = this.root.and(t -> Arrays.stream(vector3f).anyMatch(i -> t.getDirection().equals(i)));
        return (Q) this;
    }

    /**
     * Filters entities by valid status.
     *
     * @param valid the valid status to filter by
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q valid(boolean valid) {
        this.root = this.root.and(t -> t.isValid() == valid);
        return (Q) this;
    }

    /**
     * Filters entities that are inside the given area.
     *
     * @param area the area to check
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q inside(Area area) {
        this.root = this.root.and(t -> area.contains(t.getCoordinate()));
        return (Q) this;
    }

    /**
     * Filters entities that are outside the given area.
     *
     * @param area the area to check
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q outside(Area area) {
        this.root = this.root.and(t -> !area.contains(t.getCoordinate()));
        return (Q) this;
    }

    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q distance(double distance) {
        this.root = this.root.and(t -> Distance.to(t) <= distance);
        return (Q) this;
    }

    /**
     * Combines the current query predicate with another using logical AND.
     *
     * @param other another EntityQuery to AND with
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q and(EntityQuery<T> other) {
        this.root = this.root.and(other.root);
        return (Q) this;
    }

    /**
     * Combines the current query predicate with another using logical OR.
     *
     * @param other another EntityQuery to OR with
     * @return the updated EntityQuery
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q or(EntityQuery<T> other) {
        this.root = this.root.or(other.root);
        return (Q) this;
    }

    /**
     * Negates the current query predicate.
     *
     * @return the updated EntityQuery with negated predicate
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q inverse() {
        this.root = this.root.negate();
        return (Q) this;
    }

    /**
     * Marks the current EntityQuery and returns it.
     * This method uses a generic return type to maintain the specific subtype.
     *
     * @return the current EntityQuery instance
     */
    @SuppressWarnings("unchecked")
    public <Q extends EntityQuery<T>> Q mark() {
        return (Q) this;
    }
}