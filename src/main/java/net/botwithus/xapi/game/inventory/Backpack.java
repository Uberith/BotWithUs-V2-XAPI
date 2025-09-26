package net.botwithus.xapi.game.inventory;

import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.item.InventoryItem;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public class Backpack {

    /**
     * Retrieves the backpack inventory with ID 93.
     *
     * @return the backpack inventory
     */
    public static Inventory getInventory() {
        return InventoryManager.getInventory(93);
    }

    /**
     * Checks if the backpack is full.
     *
     * @return true if the backpack is full, false otherwise
     */
    public static boolean isFull() {
        Inventory backpack = getInventory();
        return backpack.getItems().stream().map(InventoryItem::getId).filter(i -> i != -1).count() == backpack.getDefinition().getCapacity();
    }

    /**
     * Checks if the backpack is empty.
     *
     * @return true if the backpack is empty, false otherwise
     */
    public static boolean isEmpty() {
        Inventory backpack = getInventory();
        return backpack.getItems().stream().map(InventoryItem::getId).allMatch(id -> id == -1);
    }

    /**
     * Retrieves all items in the backpack.
     *
     * @return a list of all items in the backpack
     */
    public static List<InventoryItem> getItems() {
        return getInventory().getItems().stream().filter(i -> !i.getName().isEmpty()).toList();
    }
    
    /**
     * Checks if the backpack contains any items with names matching the given predicate.
     *
     * @param spred the predicate to match item names
     * @param names the names to check
     * @return true if any item name matches the predicate, false otherwise
     */
    public static boolean contains(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names == null || names.length == 0) {
            return false;
        }
        var sanitizedNames = Arrays.stream(names)
                .filter(Objects::nonNull)
                .toList();
        if (sanitizedNames.isEmpty()) {
            return false;
        }
        Inventory backpack = getInventory();
        return backpack.getItems().stream()
                .map(InventoryItem::getName)
                .filter(Objects::nonNull)
                .anyMatch(name -> sanitizedNames.stream().anyMatch(candidate -> Boolean.TRUE.equals(spred.apply(name, candidate))));
    }

    /**
     * Checks if the backpack contains any items with the given names.
     *
     * @param names the names to check
     * @return true if any item name matches, false otherwise
     */
    public static boolean contains(String... names) {
        return contains(String::contentEquals, names);
    }

    /**
     * Checks if the backpack contains any items with the given IDs.
     *
     * @param ids the IDs to check
     * @return true if any item ID matches, false otherwise
     */
    public static boolean contains(int... ids) {
        Inventory backpack = getInventory();
        return backpack.getItems().stream().map(InventoryItem::getId).anyMatch(id -> Arrays.stream(ids).anyMatch(i -> i == id));
    }

    /**
     * Checks if the backpack contains any items with names matching the given patterns.
     *
     * @param namePatterns the patterns to match item names
     * @return true if any item name matches any of the patterns, false otherwise
     */
    public static boolean contains(Pattern... namePatterns) {
        Inventory backpack = getInventory();
        return backpack.getItems().stream().map(InventoryItem::getName).anyMatch(name -> Arrays.stream(namePatterns).anyMatch(p -> p.matcher(name).matches()));
    }

    /**
     * Retrieves the first item in the backpack with a name matching the given predicate.
     *
     * @param spred the predicate to match item names
     * @param names the names to check
     * @return the first matching item, or null if no match is found
     */
    public static InventoryItem getItem(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names == null || names.length == 0) {
            return null;
        }
        var sanitizedNames = Arrays.stream(names)
                .filter(Objects::nonNull)
                .toList();
        if (sanitizedNames.isEmpty()) {
            return null;
        }
        Inventory backpack = getInventory();
        return backpack.getItems().stream()
                .filter(item -> item.getName() != null && sanitizedNames.stream().anyMatch(candidate -> Boolean.TRUE.equals(spred.apply(item.getName(), candidate))))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves the first item in the backpack with the given name.
     *
     * @param names the names to check
     * @return the first matching item, or null if no match is found
     */
    public static InventoryItem getItem(String... names) {
        return getItem(String::contentEquals, names);
    }

    /**
     * Retrieves the first item in the backpack with the given ID.
     *
     * @param ids the IDs to check
     * @return the first matching item, or null if no match is found
     */
    public static InventoryItem getItem(int... ids) {
        Inventory backpack = getInventory();
        return backpack.getItems().stream().filter(item -> Arrays.stream(ids).anyMatch(i -> i == item.getId())).findFirst().orElse(null);
    }
}