package net.botwithus.xapi.query.result;

import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class EntityResultSet<T extends Entity> extends ResultSet<T> {

    /**
     * Constructs an EntityResultSet with the given results.
     *
     * @param results the list of entities
     */
    public EntityResultSet(List<T> results) {
        super(results);
    }

    /**
     * Finds the nearest entity to the given coordinate.
     *
     * @param coordinate the coordinate to compare
     * @return the nearest entity, or null if no entities are found
     */
    public T nearestTo(Coordinate coordinate) {
        List<T> copy = new ArrayList<>(results);
        List<T> sorted = copy.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(o -> Distance.between(o, coordinate)))
                .toList();
        if (!sorted.isEmpty()) {
            return sorted.get(0);
        }
        return null;
    }

    /**
     * Removes all entities in the given result set from this result set.
     *
     * @param set the result set to remove
     * @return a new EntityResultSet with the entities removed
     */
    public EntityResultSet<T> removeAll(ResultSet<T> set) {
        List<T> copy = new ArrayList<>(results);
        copy.removeAll(set.results);
        return new EntityResultSet<>(copy);
    }

    /**
     * Removes the specified entity from this result set.
     *
     * @param toRemove the entity to remove
     * @return a new EntityResultSet with the entity removed
     */
    public EntityResultSet<T> remove(T toRemove) {
        List<T> copy = new ArrayList<>(results);
        copy.remove(toRemove);
        return new EntityResultSet<>(copy);
    }

    /**
     * Finds the nearest entity to the given entity.
     *
     * @param entity the entity to compare
     * @return the nearest entity, or null if no entities are found
     */
    public T nearestTo(Entity entity) {
        return entity != null && entity.getCoordinate() != null ? nearestTo(entity.getCoordinate()) : null;
    }

    /**
     * Finds the nearest entity to the local player.
     *
     * @return the nearest entity, or null if no entities are found
     */
    public T nearest() {
        var player = LocalPlayer.self();
        if (player != null) {
            return nearestTo(player);
        }
        return null;
    }
}