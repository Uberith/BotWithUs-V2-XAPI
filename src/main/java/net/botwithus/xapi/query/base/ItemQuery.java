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
    protected final InventoryQuery inventoryQuery;

    public ItemQuery(int... inventoryId) {
        inventoryQuery = new InventoryQuery(inventoryId);
        root = item -> true;
    }

    protected void predicateChanged() {
        // subclasses override to invalidate caches
    }

    @SuppressWarnings("unchecked")
    public T id(int... ids) {
        if (ids.length == 0) {
            return (T) this;
        }
        root = root.and(i -> Arrays.stream(ids).anyMatch(id -> id == i.getId()));
        predicateChanged();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T quantity(BiFunction<Integer, Integer, Boolean> spred, int quantity) {
        root = root.and(i -> spred.apply(i.getQuantity(), quantity));
        predicateChanged();
        return (T) this;
    }

    public T quantity(int quantity) {
        return quantity((a, b) -> a.equals(b), quantity);
    }

    @SuppressWarnings("unchecked")
    public T itemTypes(ItemDefinition... itemTypes) {
        if (itemTypes.length == 0) {
            return (T) this;
        }
        root = root.and(i -> Arrays.stream(itemTypes).anyMatch(itemType -> itemType == i.getType()));
        predicateChanged();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T category(int... categories) {
        if (categories.length == 0) {
            return (T) this;
        }
        root = root.and(i -> Arrays.stream(categories).anyMatch(category -> category == i.getCategory()));
        predicateChanged();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return (T) this;
        }
        root = root.and(i -> Arrays.stream(names).anyMatch(name -> spred.apply(i.getName(), name)));
        predicateChanged();
        return (T) this;
    }

    public T name(String... names) {
        return name(String::contentEquals, names);
    }

    @SuppressWarnings("unchecked")
    public T name(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return (T) this;
        }
        root = root.and(i -> {
            String itemName = i.getName();
            return itemName != null && Arrays.stream(patterns).anyMatch(p -> p.matcher(itemName).matches());
        });
        predicateChanged();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T stackType(StackType... stackTypes) {
        if (stackTypes.length == 0) {
            return (T) this;
        }
        root = root.and(i -> Arrays.stream(stackTypes).anyMatch(stackType -> stackType == i.getStackType()));
        predicateChanged();
        return (T) this;
    }
}
