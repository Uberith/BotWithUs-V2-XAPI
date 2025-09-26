package net.botwithus.xapi.query;

import net.botwithus.rs3.item.InventoryItem;
import net.botwithus.xapi.query.base.ItemQuery;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.ResultSet;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;

public class InventoryItemQuery extends ItemQuery<InventoryItem, InventoryItemQuery> {

    private final QueryCache<ResultSet<InventoryItem>> cache = new QueryCache<>();

    public InventoryItemQuery(int... inventoryId) {
        super(inventoryId);
    }

    public static InventoryItemQuery newQuery(int... inventoryIds) {
        return new InventoryItemQuery(inventoryIds);
    }

    public static InventoryItemQuery stackableLoot(int inventoryId, int... itemIds) {
        return newQuery(inventoryId).id(itemIds).stackType(net.botwithus.rs3.cache.assets.items.StackType.ALWAYS);
    }

    public InventoryItemQuery withCache(Duration ttl) {
        cache.configure(ttl);
        inventoryQuery.withCache(ttl);
        return this;
    }

    @Override
    public ResultSet<InventoryItem> results() {
        return cache.getOrCompute(() -> {
            var inventory = inventoryQuery.results().first();
            if (inventory == null) {
                return new ResultSet<>(Collections.emptyList());
            }
            return new ResultSet<>(inventory.getItems().stream().filter(this).toList());
        });
    }

    @Override
    public Iterator<InventoryItem> iterator() {
        return results().iterator();
    }

    @Override
    public boolean test(InventoryItem inventoryItem) {
        return this.root.test(inventoryItem);
    }

    @Override
    protected void predicateChanged() {
        cache.invalidate();
        super.predicateChanged();
    }

    public InventoryItemQuery slot(int... slots) {
        if (slots.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> java.util.Arrays.stream(slots).anyMatch(i -> i == t.getSlot()));
        predicateChanged();
        return this;
    }
}
