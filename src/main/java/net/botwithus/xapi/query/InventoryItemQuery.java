package net.botwithus.xapi.query;

import net.botwithus.rs3.item.InventoryItem;
import net.botwithus.xapi.query.base.ItemQuery;
import net.botwithus.xapi.query.result.ResultSet;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A query class for filtering and retrieving inventory items based on various criteria.
 */
public class InventoryItemQuery extends ItemQuery<InventoryItem> {

    /**
     * Constructs a new InvItemQuery with the specified inventory IDs.
     *
     * @param inventoryId the inventory IDs to query
     */
    public InventoryItemQuery(int... inventoryId) {
        super(inventoryId);
    }

    /**
     * Creates a new InvItemQuery with the specified inventory IDs.
     *
     * @param inventoryIds the inventory IDs to query
     * @return a new InvItemQuery instance
     */
    public static InventoryItemQuery newQuery(int... inventoryIds) {
        return new InventoryItemQuery(inventoryIds);
    }

    /**
     * Retrieves the results of the query as a ResultSet.
     *
     * @return a ResultSet containing the filtered inventory items
     */
    @Override
    public ResultSet<InventoryItem> results() {
        return new ResultSet<>(inventoryQuery.results().first().getItems().stream().filter(this).toList());
    }

    /**
     * Returns an iterator over the elements in the result set.
     *
     * @return an Iterator over the elements in the result set
     */
    @Override
    public Iterator<InventoryItem> iterator() {
        return results().iterator();
    }

    /**
     * Tests if an inventory item matches the query predicate.
     *
     * @param inventoryItem the inventory item to test
     * @return true if the inventory item matches, false otherwise
     */
    @Override
    public boolean test(InventoryItem inventoryItem) {
        return this.root.test(inventoryItem);
    }

    /**
     * Filters inventory items by slot.
     *
     * @param slots the slots to filter by
     * @return the updated InvItemQuery
     */
    public InventoryItemQuery slot(int... slots) {
        this.root = this.root.and(t -> Arrays.stream(slots).anyMatch(i -> i == t.getSlot()));
        return this;
    }
}