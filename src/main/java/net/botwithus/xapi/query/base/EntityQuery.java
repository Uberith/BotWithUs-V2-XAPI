package net.botwithus.xapi.query.base;

import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.world.Direction;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.xapi.query.result.EntityResultSet;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class EntityQuery<T extends Entity> implements Query<T, EntityResultSet<T>> {

    protected Predicate<T> root;

    /**
     * Constructs a new EntityQuery with a default predicate.
     */
    public EntityQuery() {
        root = t -> true;
    }

    /**
     * Filters entities by type.
     *
     * @param entityType the entity types to filter by
     * @return the updated EntityQuery
     */
    public EntityQuery<T> type(EntityType... entityType) {
        this.root = this.root.and(t -> Arrays.stream(entityType).anyMatch(i -> t.getType() == i));
        return this;
    }

    /**
     * Filters entities by coordinate.
     *
     * @param coordinate the coordinates to filter by
     * @return the updated EntityQuery
     */
    public EntityQuery<T> coordinate(Coordinate... coordinate) {
        this.root = this.root.and(t -> Arrays.stream(coordinate).anyMatch(i -> t.getCoordinate().equals(i)));
        return this;
    }

    /**
     * Filters entities by direction.
     *
     * @param direction the directions to filter by
     * @return the updated EntityQuery
     */
    public EntityQuery<T> direction(Direction... direction) {
        this.root = this.root.and(t -> Arrays.stream(direction).anyMatch(i -> t.getDirection().equals(i)));
        return this;
    }

    /**
     * Filters entities by valid status.
     *
     * @param valid the valid status to filter by
     * @return the updated EntityQuery
     */
    public EntityQuery<T> valid(boolean valid) {
        this.root = this.root.and(t -> t.isValid() == valid);
        return this;
    }
}