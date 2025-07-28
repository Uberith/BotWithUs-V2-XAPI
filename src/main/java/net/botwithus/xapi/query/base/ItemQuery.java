package net.botwithus.xapi.query.base;

import net.botwithus.rs3.cache.assets.items.ItemDefinition;
import net.botwithus.rs3.cache.assets.items.StackType;
import net.botwithus.rs3.item.Item;
import net.botwithus.xapi.query.InventoryQuery;
import net.botwithus.xapi.query.result.ResultSet;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class ItemQuery<I extends Item, T extends ItemQuery<I, T>> implements Query<I, ResultSet<I>> {

    protected Predicate<I> root;

    protected InventoryQuery inventoryQuery;

    public ItemQuery(int... inventoryId) {
        inventoryQuery = new InventoryQuery(inventoryId);
        root = item -> true; // Initialize with a predicate that accepts all items
    }

    @SuppressWarnings("unchecked")
    public T id(int... ids) {
        root = root.and(i -> Arrays.stream(ids).anyMatch(id -> id == i.getId()));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T quantity(BiFunction<Integer, Integer, Boolean> spred, int quantity) {
        root = root.and(i -> spred.apply(i.getQuantity(), quantity));
        return (T) this;
    }

    public T quantity(int quantity) {
        return quantity((a, b) -> a == b, quantity);
    }

    @SuppressWarnings("unchecked")
    public T itemTypes(ItemDefinition... itemTypes) {
        root = root.and(i -> Arrays.stream(itemTypes).anyMatch(itemType -> itemType == i.getType()));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T category(int... categories) {
        root = root.and(i -> Arrays.stream(categories).anyMatch(category -> category == i.getCategory()));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        root = root.and(i -> Arrays.stream(names).anyMatch(name -> spred.apply(i.getName(), name)));
        return (T) this;
    }

    public T name(String... names) {
        return name(String::contentEquals, names);
    }

    @SuppressWarnings("unchecked")
    public T name(java.util.regex.Pattern... patterns) {
        root = root.and(i -> {
            String itemName = i.getName();
            return itemName != null && Arrays.stream(patterns).anyMatch(p -> p.matcher(itemName).matches());
        });
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T stackType(StackType... stackTypes) {
        root = root.and(i -> Arrays.stream(stackTypes).anyMatch(stackType -> stackType == i.getStackType()));
        return (T) this;
    }
}
