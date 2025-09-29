package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.items.ItemDefinition;
import net.botwithus.rs3.cache.assets.items.StackType;
import net.botwithus.rs3.item.GroundItem;
import net.botwithus.rs3.world.Area;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;
import net.botwithus.rs3.world.World;
import net.botwithus.xapi.query.base.Query;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.GroundItemResultSet;
import net.botwithus.xapi.query.result.ResultSet;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A query class for filtering and retrieving ground items based on various criteria.
 */
public class GroundItemQuery implements Query<GroundItem, ResultSet<GroundItem>> {

    protected Predicate<GroundItem> root;
    private final QueryCache<GroundItemResultSet> cache = new QueryCache<>();

    /**
     * Constructs a new GroundItemQuery with a default predicate.
     */
    @SuppressWarnings("unused")
    public GroundItemQuery() {
        root = groundItem -> true;
    }

    /**
     * Creates a new GroundItemQuery instance.
     *
     * @return a new GroundItemQuery instance
     */
    public static GroundItemQuery newQuery() {
        return new GroundItemQuery();
    }

    public GroundItemQuery withCache(Duration ttl) {
        cache.configure(ttl);
        return this;
    }

    public static GroundItemQuery lootable(int... ids) {
        return newQuery().valid(true).id(ids);
    }

    public static GroundItemQuery lootableWithin(double distance, int... ids) {
        return lootable(ids).distance(distance);
    }

    /**
     * Retrieves the results of the query.
     *
     * @return a ResultSet containing the query results
     */
    @Override
    public GroundItemResultSet results() {
        return cache.getOrCompute(() -> new GroundItemResultSet(World.getGroundItems().stream()
                .flatMap(itemStack -> itemStack.getItems().stream())
                .filter(this)
                .toList()));
    }

    public GroundItem nearestWithin(double maxDistance) {
        return results().nearestWithin(maxDistance);
    }

    /**
     * Returns an iterator over the query results.
     *
     * @return an Iterator over the query results
     */
    @NotNull
    @Override
    public Iterator<GroundItem> iterator() {
        return results().iterator();
    }

    /**
     * Tests if a ground item matches the query predicate.
     *
     * @param groundItem the ground item to test
     * @return true if the ground item matches, false otherwise
     */
    @Override
    public boolean test(GroundItem groundItem) {
        return this.root.test(groundItem);
    }

    private void invalidateCache() {
        cache.invalidate();
    }

    // ========== Item-based filtering methods ==========

    /**
     * Filters ground items by item ID.
     *
     * @param ids the item IDs to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery id(int... ids) {
        if (ids.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(ids).anyMatch(id -> id == i.getId()));
        return this;
    }

    /**
     * Filters ground items by quantity using a predicate.
     *
     * @param spred the predicate to match quantities
     * @param quantity the quantity to compare against
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery quantity(BiFunction<Integer, Integer, Boolean> spred, int quantity) {
        invalidateCache();
        this.root = this.root.and(i -> spred.apply(i.getQuantity(), quantity));
        return this;
    }

    /**
     * Filters ground items by exact quantity.
     *
     * @param quantity the exact quantity to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery quantity(int quantity) {
        return quantity((a, b) -> a.equals(b), quantity);
    }

    /**
     * Filters ground items by item types.
     *
     * @param itemTypes the item types to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery itemTypes(ItemDefinition... itemTypes) {
        if (itemTypes.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(itemTypes).anyMatch(itemType -> itemType.equals(i.getType())));
        return this;
    }

    /**
     * Filters ground items by category.
     *
     * @param categories the categories to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery category(int... categories) {
        if (categories.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(categories).anyMatch(category -> category == i.getCategory()));
        return this;
    }

    /**
     * Filters ground items by name using a predicate.
     *
     * @param spred the predicate to match names
     * @param names the names to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(names).anyMatch(name -> spred.apply(i.getName(), name)));
        return this;
    }

    /**
     * Filters ground items by name using content equality.
     *
     * @param names the names to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery name(String... names) {
        return name(String::contentEquals, names);
    }

    /**
     * Filters ground items by name using regular expression patterns.
     *
     * @param patterns the regex patterns to filter names by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery name(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> {
            String itemName = i.getName();
            return itemName != null && Arrays.stream(patterns).anyMatch(p -> p.matcher(itemName).matches());
        });
        return this;
    }

    /**
     * Filters ground items by stack type.
     *
     * @param stackTypes the stack types to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery stackType(StackType... stackTypes) {
        if (stackTypes.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(stackTypes).anyMatch(stackType -> stackType == i.getStackType()));
        return this;
    }

    // ========== GroundItem-specific filtering methods ==========

    /**
     * Filters ground items by coordinate.
     *
     * @param coordinates the coordinates to filter by
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery coordinate(Coordinate... coordinates) {
        if (coordinates.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(i -> Arrays.stream(coordinates).anyMatch(coord -> i.getStack().getCoordinate().equals(coord)));
        return this;
    }

    /**
     * Filters ground items that are inside the given area.
     *
     * @param area the area to check
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery inside(Area area) {
        invalidateCache();
        this.root = this.root.and(i -> area.contains(i.getStack().getCoordinate()));
        return this;
    }

    /**
     * Filters ground items that are outside the given area.
     *
     * @param area the area to check
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery outside(Area area) {
        invalidateCache();
        this.root = this.root.and(i -> !area.contains(i.getStack().getCoordinate()));
        return this;
    }

    /**
     * Filters ground items by distance from the player.
     *
     * @param distance the maximum distance
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery distance(double distance) {
        invalidateCache();
        this.root = this.root.and(i -> Distance.to(i.getStack().getCoordinate()) <= distance);
        return this;
    }

    /**
     * Filters ground items by their validity status.
     *
     * @param valid true to filter for valid items, false for invalid items
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery valid(boolean valid) {
        invalidateCache();
        this.root = this.root.and(i -> i.getStack().isValid() == valid);
        return this;
    }

    // ========== Utility methods ==========

    /**
     * Combines the current query predicate with another using logical AND.
     *
     * @param other another GroundItemQuery to AND with
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery and(GroundItemQuery other) {
        invalidateCache();
        this.root = this.root.and(other.root);
        return this;
    }

    /**
     * Combines the current query predicate with another using logical OR.
     *
     * @param other another GroundItemQuery to OR with
     * @return the updated GroundItemQuery
     */
    public GroundItemQuery or(GroundItemQuery other) {
        invalidateCache();
        this.root = this.root.or(other.root);
        return this;
    }

    /**
     * Negates the current query predicate.
     *
     * @return the updated GroundItemQuery with negated predicate
     */
    public GroundItemQuery invert() {
        invalidateCache();
        this.root = this.root.negate();
        return this;
    }

    /**
     * Marks the current GroundItemQuery and returns it.
     * This method maintains the specific subtype.
     *
     * @return the current GroundItemQuery instance
     */
    public GroundItemQuery mark() {
        return this;
    }
}


