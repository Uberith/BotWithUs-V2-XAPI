package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.inventories.InventoryDefinition;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.item.Item;
import net.botwithus.xapi.query.base.Query;
import net.botwithus.xapi.query.result.ResultSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * A query class for filtering and retrieving inventories based on various criteria.
 */
public class InventoryQuery implements Query<Inventory, ResultSet<Inventory>> {

    protected Predicate<Inventory> root;
    private int[] ids;

    /**
     * Constructs a new InventoryQuery with the specified IDs.
     *
     * @param ids the IDs to query
     */
    public InventoryQuery(int... ids) {
        this.ids = ids;
        if (ids.length == 0) {
            root = t -> true;
        } else {
            root = t -> Arrays.stream(ids).anyMatch(i -> i == t.getId());
        }
    }

    /**
     * Retrieves the results of the query as a ResultSet.
     *
     * @return a ResultSet containing the filtered inventories
     */
    @Override
    public ResultSet<Inventory> results() {
        return new ResultSet<>(
                Arrays.stream(ids)
                        .mapToObj(InventoryManager::getInventory) // Map IDs to Inventories
                        .filter(Objects::nonNull) // Filter out null inventories
                        .filter(this) // Apply the predicate (root.test)
                        .toList() // Collect the filtered inventories into a list
        );
    }

    /**
     * Returns an iterator over the elements in the result set.
     *
     * @return an Iterator over the elements in the result set
     */
    @Override
    public Iterator<Inventory> iterator() {
        return results().iterator();
    }

    /**
     * Tests if an inventory matches the query predicate.
     *
     * @param inventory the inventory to test
     * @return true if the inventory matches, false otherwise
     */
    @Override
    public boolean test(Inventory inventory) {
        return this.root.test(inventory);
    }

    /**
     * Filters inventories by type.
     *
     * @param types the inventory types to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery type(InventoryDefinition... types) {
        root = root.and(t -> Arrays.stream(types).anyMatch(i -> i == t.getDefinition()));
        return this;
    }

    /**
     * Filters inventories by full status.
     *
     * @param full the full status to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery isFull(boolean full) {
        root = root.and(t -> t.isFull() == full);
        return this;
    }

    /**
     * Filters inventories by the number of free slots using a custom function.
     *
     * @param func the function to compare free slots
     * @param slots the number of slots to compare
     * @return the updated InventoryQuery
     */
    public InventoryQuery freeSlots(BiFunction<Integer, Integer, Boolean> func, int slots) {
        root = root.and(t -> func.apply(t.freeSlots(), slots));
        return this;
    }

    /**
     * Filters inventories by the number of free slots.
     *
     * @param slots the number of free slots to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery freeSlots(int slots) {
        return freeSlots((a, b) -> a >= b, slots);
    }

    /**
     * Filters inventories by item IDs.
     *
     * @param itemIds the item IDs to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery contains(int... itemIds) {
        root = root.and(t -> Arrays.stream(itemIds).anyMatch(t::contains));
        return this;
    }

    /**
     * Filters inventories by containing any of the specified item names using a custom function.
     *
     * @param spred the function to compare item names
     * @param names the item names to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery contains(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var itemNames = t.getItems().stream().map(Item::getName).toList();
            return Arrays.stream(names).anyMatch(i -> itemNames.stream().anyMatch(j -> spred.apply(i, j)));
        });
        return this;
    }

    /**
     * Filters inventories by containing any of the specified item names.
     *
     * @param names the item names to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery contains(String... names) {
        return contains(String::contentEquals, names);
    }

    /**
     * Filters inventories by containing all specified item IDs.
     *
     * @param itemIds the item IDs to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery containsAll(int... itemIds) {
        root = root.and(t -> Arrays.stream(itemIds).allMatch(t::contains));
        return this;
    }

    /**
     * Filters inventories by item categories.
     *
     * @param categories the item categories to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery containsCategory(int... categories) {
        root = root.and(t -> Arrays.stream(categories).anyMatch(t::containsByCategory));
        return this;
    }

    /**
     * Filters inventories by containing all specified item categories.
     *
     * @param categories the item categories to filter by
     * @return the updated InventoryQuery
     */
    public InventoryQuery containsAllCategory(int... categories) {
        root = root.and(t -> Arrays.stream(categories).allMatch(t::containsByCategory));
        return this;
    }
}