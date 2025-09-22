package net.botwithus.xapi.game.inventory;

import net.botwithus.rs3.interfaces.Component;
import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.rs3.inventories.Inventory;
import net.botwithus.rs3.inventories.InventoryManager;
import net.botwithus.rs3.item.InventoryItem;
import net.botwithus.rs3.item.Item;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.rs3.world.Distance;
import net.botwithus.util.Rand;
import net.botwithus.xapi.query.ComponentQuery;
import net.botwithus.xapi.query.InventoryItemQuery;
import net.botwithus.xapi.query.NpcQuery;
import net.botwithus.xapi.query.SceneObjectQuery;
import net.botwithus.xapi.query.result.ResultSet;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Bank {
    private static final int PRESET_BROWSING_VARBIT_ID = 49662, SELECTED_OPTIONS_TAB_VARBIT_ID = 45191, WITHDRAW_TYPE_VARBIT_ID = 45189, WITHDRAW_X_VARP_ID = 111;
    private static final Pattern BANK_NAME_PATTERN = Pattern.compile("^(?!.*deposit).*(bank|counter).*$", Pattern.CASE_INSENSITIVE);
    private static final String LAST_PRESET_OPTION = "Load Last Preset from";
    public static int INVENTORY_ID = 95, INTERFACE_INDEX = 517, COMPONENT_INDEX = 202;
    private static final Logger logger = LoggerFactory.getLogger(Bank.class);


    private static int previousLoadedPreset = -1;

    /**
     * Opens the nearest bank.
     *
     * @return {@code true} if the bank was successfully opened, {@code false} otherwise.
     */
    public static boolean open(PermissiveScript script) {
        try {
            logger.info("Attempting find bank obj");
            var obj = SceneObjectQuery.newQuery().name(BANK_NAME_PATTERN).option("Use")
                    .or(SceneObjectQuery.newQuery().name(BANK_NAME_PATTERN).option("Bank"))
                    .or(SceneObjectQuery.newQuery().name("Shantay chest")).results().nearest();

            logger.info("Attempting find bank npc");
            var npc = NpcQuery.newQuery().option("Bank").results().nearest();
            logger.info("Bank opening initiated");
            var useObj = true;

            logger.info("Object is " + (obj != null ? "not null" : "null"));
            logger.info("Npc is " + (npc != null ? "not null" : "null"));

            if (obj != null && npc != null) {
                logger.info("Distance.to(obj): " + Distance.to(obj));
                logger.info("Distance.to(npc): " + Distance.to(npc));
                var objDist = Distance.to(obj);
                var npcDist = Distance.to(npc);
                if (!Double.isNaN(objDist) && !Double.isNaN(npcDist))
                    useObj = Distance.to(obj) < Distance.to(npc);
                logger.info("useObj: " + useObj);
            }
            if (obj != null && useObj) {
                script.info("Interacting via Object: " + obj.getName());
                var actions = obj.getOptions();
                logger.info("Available Options: " + actions);
                if (!actions.isEmpty()) {
                    var action = actions.stream().filter(i -> i != null && !i.isEmpty()).findFirst();
                    logger.info("action.isPresent(): " + action.isPresent());
                    if (action.isPresent()) {
                        logger.info("Attempting to interact with bank object using action: " + action.get());
                        var interactionResult = obj.interact(action.get());
                        script.info("Object interaction completed: " + interactionResult);
                        return interactionResult > 0;
                    } else {
                        script.warn("No valid action found for bank object");
                        return false;
                    }
                } else {
                    script.warn("No options available on bank object");
                    return false;
                }
            } else if (npc != null) {
                script.info("Interacting via NPC");
                var interactionResult = npc.interact("Bank");
                script.info("NPC interaction completed: " + interactionResult);
                return interactionResult > 0;
            }
            script.warn("No valid bank object or NPC found");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if the bank interface is currently open
     * @return true if bank is open, false otherwise
     */
    public static boolean isOpen() {
        return Interfaces.isOpen(INTERFACE_INDEX);
    }

    /**
     * Closes the bank interface.
     *
     * @return true if the interface was closed, false otherwise
     */
    public static boolean close() {
        return MiniMenu.doAction(Action.COMPONENT, 1, -1, 33882430) > 0;
    }

    /**
     * Retrieves the backpack inventory with ID 93.
     *
     * @return the backpack inventory
     */
    public static Inventory getInventory() {
        return InventoryManager.getInventory(INVENTORY_ID);
    }

    public static boolean loadLastPreset() {
        var obj = SceneObjectQuery.newQuery()
                .option(LAST_PRESET_OPTION).results().nearest();
        var npc = NpcQuery.newQuery().option(LAST_PRESET_OPTION).results().nearest();
        var useObj = true;

//        logger.debug("Object is " + (obj != null ? "not null" : "null"));
//        logger.debug("Npc is " + (npc != null ? "not null" : "null"));

        if (obj != null && npc != null) {
//            logger.debug("Distance.to(obj): " + Distance.to(obj));
//            logger.debug("Distance.to(npc): " + Distance.to(npc));
            var objDist = Distance.to(obj);
            var npcDist = Distance.to(npc);
            if (!Double.isNaN(objDist) && !Double.isNaN(npcDist))
                useObj = Distance.to(obj) < Distance.to(npc);
//            logger.debug("useObj: " + useObj);
        }
        if (obj != null && useObj) {
//            logger.debug("Interacting via Object: " + obj.getName());
            return obj.interact(LAST_PRESET_OPTION) > 0;
        } else if (npc != null) {
//            logger.debug("Interacting via Npc: " + npc.getName());
            return npc.interact(LAST_PRESET_OPTION) > 0;
        }
        return false;
    }

    /**
     * Gets all the items in the players bank
     *
     * @return returns an array containing all items in the bank.
     */
    public static Item[] getItems() {
        return InventoryItemQuery.newQuery(INVENTORY_ID).results().stream().filter(i -> i.getId() != -1).toArray(Item[]::new);
    }

    /**
     * Gets the count of a specific item in the bank.
     *
     * @param results the query results specifying the item to count.
     * @return returns an integer representing the count of the item
     */
    public static int count(ResultSet<InventoryItem> results) {
        return results.stream().mapToInt(Item::getQuantity).sum();
    }

    /**
     * Gets the first item matching the predicate.
     *
     * @param query the predicate specifying the item to count.
     * @return returns the item, or null if not found.
     */
    public static Item first(InventoryItemQuery query) {
        return query.results().first();
    }

    /**
     * Determines if the bank is empty
     *
     * @return returns true if empty, false if not.
     */
    public static boolean isEmpty() {
        return getItems().length == 0;
    }

    public static boolean interact(int slot, int option) {
        ResultSet<InventoryItem> results = InventoryItemQuery.newQuery(INVENTORY_ID).slot(slot).results();
        var item = results.first();
        if (item != null) {
            logger.info("[Inventory#interact(slot, option)]: " + item.getId());
            ResultSet<Component> queryResults = ComponentQuery.newQuery(INTERFACE_INDEX).id(COMPONENT_INDEX).itemId(item.getId()).results();
            logger.info("[Inventory#interact(slot, option)]: QueryResults: " + queryResults.size());
            var result = queryResults.first();
            return result != null && result.interact(option) > 0;
        }
        return false;
    }

    /**
     * Determines if the bank contains an item.
     *
     * @param query the predicate specifying the item to count.
     * @return returns the item, or null if not found.
     */
    public static boolean contains(InventoryItemQuery query) {
        return count(query.results()) > 0;
    }

    public static boolean contains(String... itemNames) {
        return !InventoryItemQuery.newQuery(INVENTORY_ID).name(itemNames).results().isEmpty();
    }

    public static boolean contains(Pattern itemNamePattern) {
        return !InventoryItemQuery.newQuery(INVENTORY_ID).name(itemNamePattern).results().isEmpty();
    }

    public static int getCount(String... itemNames) {
        return count(InventoryItemQuery.newQuery(INVENTORY_ID).name(itemNames).results());
    }

    public static int getCount(Pattern namePattern) {
        return count(InventoryItemQuery.newQuery(INVENTORY_ID).name(namePattern).results());
    }

    /**
     * Withdraws an item from the bank
     *
     * @param query  the query specifying the item to withdraw.
     * @param option the doAction option to execute on the item.
     */
    public static boolean withdraw(InventoryItemQuery query, int option) {
        setTransferOption(TransferOptionType.ALL);
        var item = query.results().first();
        if (item != null) {
            logger.info("Item: " + item.getName());
        } else {
            logger.info("Item is null");
        }
        return item != null && interact(item.getSlot(), option);
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param itemName The name of the item to withdraw.
     * @param option   The option to withdraw.
     * @return True if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(String itemName, int option) {
        if (itemName != null && !itemName.isEmpty()) {
            return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(itemName), option);
        }
        return false;
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param itemId The ID of the item to withdraw.
     * @param option The option of the item to withdraw.
     * @return True if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(int itemId, int option) {
        if (itemId >= 0) {
            return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).id(itemId), option);
        }
        return false;
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param pattern The pattern of the item to withdraw.
     * @param option  The option of the item to withdraw.
     * @return true if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(Pattern pattern, int option) {
        if (pattern != null) {
            return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(pattern), option);
        }
        return false;
    }

    /**
     * Withdraws all of a given item from the inventory.
     *
     * @param name The name of the item to withdraw.
     * @return true if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdrawAll(String name) {
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(name), 1);
    }

    public static boolean withdrawAll(int id) {
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).id(id), 1);
    }

    public static boolean withdrawAll(Pattern pattern) {
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(pattern), 1);
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositAll() {
        setTransferOption(TransferOptionType.ALL);
        var comp = ComponentQuery.newQuery(INTERFACE_INDEX).option("Deposit carried items").results().first();
        return comp != null && comp.interact(1) > 0;
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositEquipment() {
        Component component = ComponentQuery.newQuery(INTERFACE_INDEX).id(42).results().first();
        return component != null && component.interact(1) > 0;
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositBackpack() {
        Component component = ComponentQuery.newQuery(INTERFACE_INDEX).id(39).results().first();
        return component != null && component.interact(1) > 0;
    }


    /**
     * Attempts to deposit an item from the given {@link InventoryItemQuery}.
     *
     * @param script The script to use for depositing the item.
     * @param query  The query to use for finding the item to deposit.
     * @param option The option to use when depositing the item.
     * @return {@code true} if the item was successfully deposited, {@code false} otherwise.
     */
    public static boolean deposit(PermissiveScript script, ComponentQuery query, int option) {
        var item = query.results().first();
        return deposit(script, item, option);
    }

    public static boolean depositAll(PermissiveScript script, ComponentQuery query) {
        var item = query.results().first();
        return deposit(script, item, 1);//item.getOptions().contains("Deposit-All") ? 7 : 1);
    }

    public static boolean deposit(PermissiveScript script, Component comp, int option) {
        setTransferOption(TransferOptionType.ALL);
        var val = comp != null && comp.interact(option) > 0;
        if (val) script.delay(Rand.nextInt(1, 2));
        return val;
    }

    public static boolean depositAll(PermissiveScript script, String... itemNames) {
        return !InventoryItemQuery.newQuery(93).name(itemNames).results().stream().map(Item::getId).distinct().map(
                i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))
        ).toList().contains(false);
    }

    public static boolean depositAll(PermissiveScript script, int... itemIds) {
        return !InventoryItemQuery.newQuery(93).id(itemIds).results().stream().map(Item::getId).distinct().map(
                i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))
        ).toList().contains(false);
    }

    public static boolean depositAll(PermissiveScript script, Pattern... patterns) {
        return !InventoryItemQuery.newQuery(93).name(patterns).results().stream().map(Item::getId).distinct().map(
                i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))
        ).toList().contains(false);
    }

    public static boolean depositAllExcept(PermissiveScript script, String... itemNames) {
        var nameSet = new HashSet<>(Arrays.asList(itemNames));
        var idMap = Backpack.getItems().stream().filter(i -> Arrays.stream(itemNames).toList().contains(i)).collect(Collectors.toMap(Item::getId, Item::getName));
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        i -> !nameSet.contains(idMap.get(i.getItemId())) && (i.getOptions().contains("Deposit-All") || i.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        return !items.stream().map(i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))).toList().contains(false);
    }

    public static boolean depositAllExcept(PermissiveScript script, int... ids) {
        var idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        i -> !idSet.contains(i.getItemId()) && (i.getOptions().contains("Deposit-All") || i.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        return !items.stream().map(i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))).toList().contains(false);
    }

    public static boolean depositAllExcept(PermissiveScript script, Pattern... patterns) {
        var idMap = Backpack.getItems().stream().filter(i -> i.getName() != null && Arrays.stream(patterns).map(p -> p.matcher(i.getName()).matches()).toList().contains(true))
                .collect(Collectors.toMap(Item::getId, Item::getName));
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        i -> !idMap.containsKey(i.getItemId()) && (i.getOptions().contains("Deposit-All") || i.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        return !items.stream().map(i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))).toList().contains(false);
    }

    /**
     * Deposits an item into the inventory.
     *
     * @param itemId The ID of the item to deposit.
     * @param option The option to use when depositing the item.
     * @return True if the item was successfully deposited, false otherwise.
     */
    public static boolean deposit(PermissiveScript script, int itemId, int option) {
        return deposit(script, ComponentQuery.newQuery(517).itemId(itemId), option);
    }

    /**
     * Deposits an item into the inventory.
     *
     * @param name   The name of the item to deposit.
     * @param spred  The spread function to use when searching for the item.
     * @param option The option to use when depositing the item.
     * @return True if the item was successfully deposited, false otherwise.
     */
    public static boolean deposit(PermissiveScript script, String name, BiFunction<String, CharSequence, Boolean> spred, int option) {
        return deposit(script, ComponentQuery.newQuery(517).itemName(name, spred), option);
    }

    /**
     * Deposits an amount of money into an account.
     *
     * @param name   The name of the account to deposit into.
     * @param option The amount of money to deposit.
     * @return True if the deposit was successful, false otherwise.
     */
    public static boolean deposit(PermissiveScript script, String name, int option) {
        return deposit(script, name, String::contentEquals, option);
    }

    /**
     * Loads the given preset number.
     *
     * @param presetNumber the preset number to load
     * @return true if the preset was successfully loaded, false otherwise
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    // TODO: Update to no longer use MiniMenu.doAction
    public static boolean loadPreset(PermissiveScript script, int presetNumber) {
        int presetBrowsingValue = VarDomain.getVarBitValue(PRESET_BROWSING_VARBIT_ID);
        if ((presetNumber >= 10 && presetBrowsingValue < 1) || (presetNumber < 10 && presetBrowsingValue > 0)) {
            MiniMenu.doAction(Action.COMPONENT, 1, 100, 33882231);
            script.delay(Rand.nextInt(1, 2));
        }
        var result = MiniMenu.doAction(Action.COMPONENT, 1, presetNumber % 9,33882231) > 0;
        if (result) {
            previousLoadedPreset = presetNumber;
        }
        return result;
    }

    /**
     * Gets the value of a varbit in the inventory.
     *
     * @param slot     The inventory slot to check.
     * @param varbitId The varbit id to check.
     * @return The value of the varbit.
     */
    public static int getVarbitValue(int slot, int varbitId) {
        Inventory inventory = getInventory();
        if (inventory == null) {
            return Integer.MIN_VALUE;
        }

        return inventory.getVarbitValue(slot, varbitId);
    }

    public static boolean setTransferOption(TransferOptionType transferoptionType) {
        var depositOptionState = VarDomain.getVarBitValue(WITHDRAW_TYPE_VARBIT_ID);
        return depositOptionState == transferoptionType.getVarbitStateValue() || MiniMenu.doAction(Action.COMPONENT, 1,-1, 33882215) > 0;
    }

    public static int getPreviousLoadedPreset() {
        return previousLoadedPreset;
    }
}

enum TransferOptionType {
    ONE(2),
    FIVE(3),
    TEN(4),
    ALL(7),
    X(5);

    private int varbitStateValue;

    TransferOptionType(int varbitStateValue) {
        this.varbitStateValue = varbitStateValue;
    }

    public int getVarbitStateValue() {
        return varbitStateValue;
    }
}