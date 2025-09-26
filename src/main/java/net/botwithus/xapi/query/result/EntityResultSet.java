package net.botwithus.xapi.query.result;

import net.botwithus.rs3.entities.Entity;
import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * Wraps a collection into an EntityResultSet, filtering out nulls.
     *
     * @param entities source entities
     * @param <E> entity type
     * @return result set with non-null entities
     */
    public static <E extends Entity> EntityResultSet<E> wrap(Collection<E> entities) {
        Objects.requireNonNull(entities, "entities");
        return new EntityResultSet<>(entities.stream().filter(Objects::nonNull).toList());
    }

    /**
     * Wraps a collection into an EntityResultSet sorted by distance to the supplied coordinate.
     *
     * @param entities source entities
     * @param coordinate reference coordinate
     * @param <E> entity type
     * @return result set sorted by increasing distance
     */
    public static <E extends Entity> EntityResultSet<E> sortedByDistance(Collection<E> entities, Coordinate coordinate) {
        Objects.requireNonNull(entities, "entities");
        Objects.requireNonNull(coordinate, "coordinate");
        return new EntityResultSet<>(entities.stream()
                .filter(Objects::nonNull)
                .sorted(distanceComparator(coordinate))
                .toList());
    }

    /**
     * Finds the nearest entity to the given coordinate.
     *
     * @param coordinate the coordinate to compare
     * @return the nearest entity, or null if no entities are found
     */
    public T nearestTo(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }
        return sortedBy(coordinate).map(list -> list.get(0)).orElse(null);
    }

    /**
     * Finds the nearest entity to the given coordinate within the provided distance.
     *
     * @param coordinate reference coordinate
     * @param maxDistance maximum allowed distance
     * @return nearest entity within distance or {@code null}
     */
    public T nearestWithin(Coordinate coordinate, double maxDistance) {
        if (coordinate == null) {
            return null;
        }
        return filteredByDistance(coordinate, maxDistance).map(list -> list.get(0)).orElse(null);
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
     * Finds the nearest entity to the given entity within the provided distance.
     *
     * @param entity reference entity
     *     * @param maxDistance maximum allowed distance
     * @return nearest entity within distance or {@code null}
     */
    public T nearestWithin(Entity entity, double maxDistance) {
        return entity != null ? nearestWithin(entity.getCoordinate(), maxDistance) : null;
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

    /**
     * Finds the nearest entity to the local player within the provided distance.
     *
     * @param maxDistance maximum allowed distance
     * @return nearest entity within distance or {@code null}
     */
    public T nearestWithin(double maxDistance) {
        var player = LocalPlayer.self();
        if (player != null) {
            return nearestWithin(player, maxDistance);
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

    private Optional<List<T>> sortedBy(Coordinate coordinate) {
        if (results.isEmpty()) {
            return Optional.empty();
        }
        List<T> copy = new ArrayList<>(results);
        copy.removeIf(Objects::isNull);
        copy.sort(distanceComparator(coordinate));
        return copy.isEmpty() ? Optional.empty() : Optional.of(copy);
    }

    private Optional<List<T>> filteredByDistance(Coordinate coordinate, double maxDistance) {
        if (results.isEmpty()) {
            return Optional.empty();
        }
        List<T> copy = new ArrayList<>(results);
        copy.removeIf(Objects::isNull);
        copy.removeIf(entity -> Distance.between(entity, coordinate) > maxDistance);
        copy.sort(distanceComparator(coordinate));
        return copy.isEmpty() ? Optional.empty() : Optional.of(copy);
    }

    private static <E extends Entity> Comparator<E> distanceComparator(Coordinate coordinate) {
        return Comparator.comparingDouble(entity -> Distance.between(entity, coordinate));
    }
}
