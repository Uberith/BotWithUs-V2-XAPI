package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.inventories.InventoryDefinition;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.item.Item;
import net.botwithus.xapi.query.base.Query;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.ResultSet;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class InventoryQuery implements Query<Inventory, ResultSet<Inventory>> {

    protected Predicate<Inventory> root;
    private final QueryCache<ResultSet<Inventory>> cache = new QueryCache<>();
    private int[] ids;

    public InventoryQuery(int... ids) {
        this.ids = ids;
        if (ids.length == 0) {
            root = t -> true;
        } else {
            root = t -> Arrays.stream(ids).anyMatch(i -> i == t.getId());
        }
    }

    public InventoryQuery withCache(Duration ttl) {
        cache.configure(ttl);
        return this;
    }

    public static InventoryQuery containingAny(int inventoryId, int... itemIds) {
        return new InventoryQuery(inventoryId).contains(itemIds);
    }

    @Override
    public ResultSet<Inventory> results() {
        return cache.getOrCompute(() -> new ResultSet<>(
                Arrays.stream(ids)
                        .mapToObj(InventoryManager::getInventory)
                        .filter(Objects::nonNull)
                        .filter(this)
                        .toList()
        ));
    }

    @Override
    public Iterator<Inventory> iterator() {
        return results().iterator();
    }

    @Override
    public boolean test(Inventory inventory) {
        return this.root.test(inventory);
    }

    public InventoryQuery type(InventoryDefinition... types) {
        invalidateCache();
        root = root.and(t -> Arrays.stream(types).anyMatch(i -> i == t.getDefinition()));
        return this;
    }

    public InventoryQuery isFull(boolean full) {
        invalidateCache();
        root = root.and(t -> t.isFull() == full);
        return this;
    }

    public InventoryQuery freeSlots(BiFunction<Integer, Integer, Boolean> func, int slots) {
        invalidateCache();
        root = root.and(t -> func.apply(t.freeSlots(), slots));
        return this;
    }

    public InventoryQuery freeSlots(int slots) {
        return freeSlots((a, b) -> a >= b, slots);
    }

    public InventoryQuery contains(int... itemIds) {
        invalidateCache();
        root = root.and(t -> Arrays.stream(itemIds).anyMatch(t::contains));
        return this;
    }

    public InventoryQuery contains(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return this;
        }
        invalidateCache();
        this.root = this.root.and(t -> {
            var itemNames = t.getItems().stream().map(Item::getName).toList();
            return Arrays.stream(names).anyMatch(i -> itemNames.stream().anyMatch(j -> spred.apply(i, j)));
        });
        return this;
    }

    public InventoryQuery contains(String... names) {
        return contains(String::contentEquals, names);
    }

    public InventoryQuery containsAll(int... itemIds) {
        invalidateCache();
        root = root.and(t -> Arrays.stream(itemIds).allMatch(t::contains));
        return this;
    }

    public InventoryQuery containsCategory(int... categories) {
        invalidateCache();
        root = root.and(t -> Arrays.stream(categories).anyMatch(t::containsByCategory));
        return this;
    }

    public InventoryQuery containsAllCategory(int... categories) {
        invalidateCache();
        root = root.and(t -> Arrays.stream(categories).allMatch(t::containsByCategory));
        return this;
    }

    private void invalidateCache() {
        cache.invalidate();
    }
}
