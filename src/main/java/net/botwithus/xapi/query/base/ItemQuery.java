package net.botwithus.xapi.query.base;

import net.botwithus.rs3.cache.assets.items.ItemDefinition;
import net.botwithus.rs3.cache.assets.items.StackType;
import net.botwithus.rs3.item.Item;
import net.botwithus.xapi.query.InventoryQuery;
import net.botwithus.xapi.query.result.ResultSet;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public abstract class ItemQuery<T extends Item> implements Query<T, ResultSet<T>> {

    protected Predicate<T> root;

    protected InventoryQuery inventoryQuery;

    public ItemQuery(int... inventoryId) {
        inventoryQuery = new InventoryQuery(inventoryId);
    }

    public ItemQuery<T> id(int... ids) {
        root = root.and(i -> Arrays.stream(ids).anyMatch(id -> id == i.getId()));
        return this;
    }

    public ItemQuery<T> quantity(BiFunction<Integer, Integer, Boolean> spred, int quantity) {
        root = root.and(i -> spred.apply(i.getQuantity(), quantity));
        return this;
    }

    public ItemQuery<T> quantity(int quantity) {
        return quantity((a, b) -> a == b, quantity);
    }

    public ItemQuery<T> itemTypes(ItemDefinition... itemTypes) {
        root = root.and(i -> Arrays.stream(itemTypes).anyMatch(itemType -> itemType == i.getType()));
        return this;
    }

    public ItemQuery<T> category(int... categories) {
        root = root.and(i -> Arrays.stream(categories).anyMatch(category -> category == i.getCategory()));
        return this;
    }

    public ItemQuery<T> name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        root = root.and(i -> Arrays.stream(names).anyMatch(name -> spred.apply(i.getName(), name)));
        return this;
    }

    public ItemQuery<T> name(String... names) {
        return name(String::contentEquals, names);
    }

    public ItemQuery<T> stackType(StackType... stackTypes) {
        root = root.and(i -> Arrays.stream(stackTypes).anyMatch(stackType -> stackType == i.getStackType()));
        return this;
    }
}
