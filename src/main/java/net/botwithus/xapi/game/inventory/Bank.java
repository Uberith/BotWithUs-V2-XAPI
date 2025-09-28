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
    public static boolean open() {
        try {
            logger.debug("Starting bank open attempt");
            var objectQuery = SceneObjectQuery.newQuery().name(BANK_NAME_PATTERN).option("Use")
                    .or(SceneObjectQuery.newQuery().name(BANK_NAME_PATTERN).option("Bank"))
                    .or(SceneObjectQuery.newQuery().name("Shantay chest"));
            var obj = objectQuery.results().nearest();

            var npcQuery = NpcQuery.newQuery().option("Bank");
            var npc = npcQuery.results().nearest();
            logger.debug("Bank candidates resolved: objectName={}, npcName={}",
                    obj != null ? obj.getName() : "none",
                    npc != null ? npc.getName() : "none");

            var useObj = true;
            if (obj != null && npc != null) {
                var objDist = Distance.to(obj);
                var npcDist = Distance.to(npc);
                logger.debug("Candidate distances -> object: {}, npc: {}", objDist, npcDist);
                if (!Double.isNaN(objDist) && !Double.isNaN(npcDist)) {
                    useObj = objDist < npcDist;
                }
                logger.debug("Interaction source resolved to {}", useObj ? "object" : "npc");
            }

            if (obj != null && useObj) {
                var actions = obj.getOptions();
                logger.debug("Attempting object interaction -> name={}, options={}", obj.getName(), actions);
                var action = actions.stream().filter(option -> option != null && !option.isEmpty()).findFirst();
                if (action.isPresent()) {
                    var option = action.get();
                    logger.debug("Invoking object action {}", option);
                    var interactionResult = obj.interact(option);
                    logger.debug("Object interaction result={}", interactionResult);
                    return interactionResult > 0;
                } else {
                    logger.warn("No valid action found for bank object {}", obj.getName());
                    return false;
                }
            } else if (npc != null) {
                logger.debug("Attempting NPC interaction -> name={}", npc.getName());
                var interactionResult = npc.interact("Bank");
                logger.debug("NPC interaction result={}", interactionResult);
                return interactionResult > 0;
            }

            logger.warn("No valid bank object or NPC found during open attempt");
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while opening bank", e);
            return false;
        }
    }

    /**
     * Checks if the bank interface is currently open
     * @return true if bank is open, false otherwise
     */
    public static boolean isOpen() {
        var open = Interfaces.isOpen(INTERFACE_INDEX);
        logger.debug("Bank interface open state -> {}", open);
        return open;
    }

    /**
     * Closes the bank interface.
     *
     * @return true if the interface was closed, false otherwise
     */
    public static boolean close() {
        logger.debug("Close bank request");
        var result = MiniMenu.doAction(Action.COMPONENT, 1, -1, 33882430) > 0;
        logger.debug("Close bank result -> success={}", result);
        return result;
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
        logger.debug("Load last preset request");
        var obj = SceneObjectQuery.newQuery()
                .option(LAST_PRESET_OPTION).results().nearest();
        var npc = NpcQuery.newQuery().option(LAST_PRESET_OPTION).results().nearest();
        logger.debug("Last preset candidates -> object={}, npc={}",
                obj != null ? obj.getName() : "none",
                npc != null ? npc.getName() : "none");

        var useObj = true;
        if (obj != null && npc != null) {
            var objDist = Distance.to(obj);
            var npcDist = Distance.to(npc);
            logger.debug("Last preset distances -> object: {}, npc: {}", objDist, npcDist);
            if (!Double.isNaN(objDist) && !Double.isNaN(npcDist)) {
                useObj = objDist < npcDist;
            }
            logger.debug("Last preset interaction source -> {}", useObj ? "object" : "npc");
        }
        if (obj != null && useObj) {
            logger.debug("Interacting with preset object {}", obj.getName());
            var result = obj.interact(LAST_PRESET_OPTION);
            logger.debug("Preset object interaction result={}", result);
            return result > 0;
        } else if (npc != null) {
            logger.debug("Interacting with preset NPC {}", npc.getName());
            var result = npc.interact(LAST_PRESET_OPTION);
            logger.debug("Preset NPC interaction result={}", result);
            return result > 0;
        }
        logger.warn("No valid object or NPC found for last preset request");
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
        logger.debug("Bank component interact request -> slot={}, option={}", slot, option);
        ResultSet<InventoryItem> results = InventoryItemQuery.newQuery(INVENTORY_ID).slot(slot).results();
        var item = results.first();
        if (item == null) {
            logger.debug("Bank component interact aborted -> no item at slot {}", slot);
            return false;
        }

        logger.debug("Bank component interact matched itemId={}, name={}, slot={}", item.getId(), item.getName(), item.getSlot());
        ResultSet<Component> queryResults = ComponentQuery.newQuery(INTERFACE_INDEX)
                .id(COMPONENT_INDEX)
                .itemId(item.getId())
                .results();

        logger.debug("Bank component interact found {} component candidates", queryResults.size());
        var component = queryResults.first();
        if (component == null) {
            logger.warn("No bank component found for slot {} itemId {}", slot, item.getId());
            return false;
        }

        var interactionResult = component.interact(option);
        logger.debug("Bank component interact result={} for componentId={}", interactionResult, component.getItemId());
        return interactionResult > 0;
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
        logger.debug("Withdraw request -> option={}", option);
        setTransferOption(TransferOptionType.ALL);
        var item = query.results().first();
        if (item == null) {
            logger.debug("Withdraw request failed -> no matching item");
            return false;
        }

        logger.debug("Withdraw executing -> itemId={}, name={}, slot={}", item.getId(), item.getName(), item.getSlot());
        var success = interact(item.getSlot(), option);
        logger.debug("Withdraw result -> success={}, option={}, itemId={}", success, option, item.getId());
        return success;
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param itemName The name of the item to withdraw.
     * @param option   The option to withdraw.
     * @return True if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(String itemName, int option) {
        logger.debug("Withdraw by name request -> name={}, option={}", itemName, option);
        if (itemName == null || itemName.isEmpty()) {
            logger.debug("Withdraw by name aborted -> name is empty");
            return false;
        }
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(itemName), option);
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param itemId The ID of the item to withdraw.
     * @param option The option of the item to withdraw.
     * @return True if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(int itemId, int option) {
        logger.debug("Withdraw by id request -> id={}, option={}", itemId, option);
        if (itemId < 0) {
            logger.debug("Withdraw by id aborted -> id is negative");
            return false;
        }
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).id(itemId), option);
    }

    /**
     * Withdraws an item from the inventory.
     *
     * @param pattern The pattern of the item to withdraw.
     * @param option  The option of the item to withdraw.
     * @return true if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdraw(Pattern pattern, int option) {
        logger.debug("Withdraw by pattern request -> option={}, pattern={}", option, pattern);
        if (pattern == null) {
            logger.debug("Withdraw by pattern aborted -> pattern is null");
            return false;
        }
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(pattern), option);
    }

    /**
     * Withdraws all of a given item from the inventory.
     *
     * @param name The name of the item to withdraw.
     * @return true if the item was successfully withdrawn, false otherwise.
     */
    public static boolean withdrawAll(String name) {
        logger.debug("Withdraw-all by name request -> name={}", name);
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(name), 1);
    }

    public static boolean withdrawAll(int id) {
        logger.debug("Withdraw-all by id request -> id={}", id);
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).id(id), 1);
    }

    public static boolean withdrawAll(Pattern pattern) {
        logger.debug("Withdraw-all by pattern request -> pattern={}", pattern);
        return withdraw(InventoryItemQuery.newQuery(INVENTORY_ID).name(pattern), 1);
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositAll() {
        logger.debug("Deposit all carried items request");
        setTransferOption(TransferOptionType.ALL);
        var comp = ComponentQuery.newQuery(INTERFACE_INDEX)
                .option("Deposit carried items")
                .results()
                .first();
        if (comp == null) {
            logger.warn("Deposit all component not found");
            return false;
        }
        var result = comp.interact(1);
        logger.debug("Deposit all interaction result={}", result);
        return result > 0;
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositEquipment() {
        logger.debug("Deposit equipment request");
        Component component = ComponentQuery.newQuery(INTERFACE_INDEX).id(42).results().first();
        if (component == null) {
            logger.warn("Deposit equipment component not found");
            return false;
        }
        var result = component.interact(1);
        logger.debug("Deposit equipment interaction result={}", result);
        return result > 0;
    }

    /**
     * Deposits all items in the player's bank.
     *
     * @return true if the items were successfully deposited, false otherwise
     */
    public static boolean depositBackpack() {
        logger.debug("Deposit backpack request");
        Component component = ComponentQuery.newQuery(INTERFACE_INDEX).id(39).results().first();
        if (component == null) {
            logger.warn("Deposit backpack component not found");
            return false;
        }
        var result = component.interact(1);
        logger.debug("Deposit backpack interaction result={}", result);
        return result > 0;
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
        logger.debug("Deposit via component query -> option={}", option);
        var component = query.results().first();
        if (component == null) {
            logger.debug("Deposit via component query aborted -> no component match");
            return false;
        }
        logger.debug("Deposit via component query using componentId={} itemId={}", component.getItemId(), component.getItemId());
        return deposit(script, component, option);
    }

    public static boolean depositAll(PermissiveScript script, ComponentQuery query) {
        logger.debug("Deposit-all via component query request");
        var component = query.results().first();
        if (component == null) {
            logger.debug("Deposit-all via component query aborted -> no component match");
            return false;
        }
        return deposit(script, component, 1);
    }

    public static boolean deposit(PermissiveScript script, Component comp, int option) {
        logger.debug("Deposit component request -> option={}", option);
        setTransferOption(TransferOptionType.ALL);
        if (comp == null) {
            logger.warn("Deposit component request failed -> component not found");
            return false;
        }
        var interactionResult = comp.interact(option);
        logger.debug("Deposit component interaction result={} for componentId={}", interactionResult, comp.getItemId());
        if (interactionResult > 0) {
            script.delay(Rand.nextInt(1, 2));
            return true;
        }
        return false;
    }

    public static boolean depositAll(PermissiveScript script, String... itemNames) {
        var namesDescription = itemNames == null ? "null" : Arrays.toString(itemNames);
        var ids = InventoryItemQuery.newQuery(93).name(itemNames).results().stream()
                .map(Item::getId)
                .distinct()
                .toList();
        logger.debug("Deposit-all by names -> names={}, distinctIds={}", namesDescription, ids.size());
        var results = ids.stream()
                .map(id -> depositAll(script, ComponentQuery.newQuery(517).itemId(id)))
                .toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all by names result -> success={}", success);
        return success;
    }

    public static boolean depositAll(PermissiveScript script, int... itemIds) {
        var idsDescription = Arrays.toString(itemIds);
        var ids = InventoryItemQuery.newQuery(93).id(itemIds).results().stream()
                .map(Item::getId)
                .distinct()
                .toList();
        logger.debug("Deposit-all by ids -> requestIds={}, distinctMatches={}", idsDescription, ids.size());
        var results = ids.stream()
                .map(id -> depositAll(script, ComponentQuery.newQuery(517).itemId(id)))
                .toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all by ids result -> success={}", success);
        return success;
    }

    public static boolean depositAll(PermissiveScript script, Pattern... patterns) {
        var patternDescription = patterns == null ? "null" : Arrays.toString(patterns);
        var ids = InventoryItemQuery.newQuery(93).name(patterns).results().stream()
                .map(Item::getId)
                .distinct()
                .toList();
        logger.debug("Deposit-all by patterns -> patterns={}, distinctMatches={}", patternDescription, ids.size());
        var results = ids.stream()
                .map(id -> depositAll(script, ComponentQuery.newQuery(517).itemId(id)))
                .toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all by patterns result -> success={}", success);
        return success;
    }

    public static boolean depositAllExcept(PermissiveScript script, String... itemNames) {
        var names = itemNames == null ? new String[0] : itemNames;
        var protectedNames = Arrays.stream(names)
                .filter(name -> name != null && !name.isEmpty())
                .collect(Collectors.toSet());
        var protectedIds = Backpack.getItems().stream()
                .filter(item -> item.getName() != null && protectedNames.contains(item.getName()))
                .map(Item::getId)
                .collect(Collectors.toSet());
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        component -> !protectedIds.contains(component.getItemId()) && (component.getOptions().contains("Deposit-All") || component.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        logger.debug("Deposit-all-except by names -> protectedNames={}, protectedIds={}, candidates={}", protectedNames.size(), protectedIds.size(), items.size());
        var results = items.stream().map(id -> depositAll(script, ComponentQuery.newQuery(517).itemId(id))).toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all-except by names result -> success={}", success);
        return success;
    }

    public static boolean depositAllExcept(PermissiveScript script, int... ids) {
        var idSet = Arrays.stream(ids).boxed().collect(Collectors.toSet());
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        i -> !idSet.contains(i.getItemId()) && (i.getOptions().contains("Deposit-All") || i.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        logger.debug("Deposit-all-except by ids -> protectedIds={}, candidates={}", idSet.size(), items.size());
        var results = items.stream().map(i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))).toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all-except by ids result -> success={}", success);
        return success;
    }

    public static boolean depositAllExcept(PermissiveScript script, Pattern... patterns) {
        var idMap = Backpack.getItems().stream().filter(i -> i.getName() != null && Arrays.stream(patterns).map(p -> p.matcher(i.getName()).matches()).toList().contains(true))
                .collect(Collectors.toMap(Item::getId, Item::getName));
        var items = ComponentQuery.newQuery(517).results().stream().filter(
                        i -> !idMap.containsKey(i.getItemId()) && (i.getOptions().contains("Deposit-All") || i.getOptions().contains("Deposit-1")))
                .map(Component::getItemId)
                .collect(Collectors.toSet());
        logger.debug("Deposit-all-except by patterns -> protectedIds={}, candidates={}", idMap.size(), items.size());
        var results = items.stream().map(i -> depositAll(script, ComponentQuery.newQuery(517).itemId(i))).toList();
        var success = !results.contains(false);
        logger.debug("Deposit-all-except by patterns result -> success={}", success);
        return success;
    }

    /**
     * Deposits an item into the inventory.
     *
     * @param itemId The ID of the item to deposit.
     * @param option The option to use when depositing the item.
     * @return True if the item was successfully deposited, false otherwise.
     */
    public static boolean deposit(PermissiveScript script, int itemId, int option) {
        logger.debug("Deposit by id request -> itemId={}, option={}", itemId, option);
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
        logger.debug("Deposit by name request -> name={}, option={}", name, option);
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
        logger.debug("Deposit by name (contentEquals) request -> name={}, option={}", name, option);
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
        logger.debug("Load preset request -> presetNumber={}", presetNumber);
        int presetBrowsingValue = VarDomain.getVarBitValue(PRESET_BROWSING_VARBIT_ID);
        logger.debug("Preset browsing state -> value={}", presetBrowsingValue);
        var requiresToggle = (presetNumber >= 10 && presetBrowsingValue < 1) || (presetNumber < 10 && presetBrowsingValue > 0);
        if (requiresToggle) {
            logger.debug("Adjusting preset browsing tab for preset {}", presetNumber);
            MiniMenu.doAction(Action.COMPONENT, 1, 100, 33882231);
            script.delay(Rand.nextInt(1, 2));
        }
        var index = presetNumber % 9;
        var result = MiniMenu.doAction(Action.COMPONENT, 1, index, 33882231) > 0;
        logger.debug("Preset load interaction result -> success={}, index={}", result, index);
        if (result) {
            previousLoadedPreset = presetNumber;
            logger.debug("Recorded previous loaded preset={}", previousLoadedPreset);
        } else {
            logger.warn("Failed to load preset {}", presetNumber);
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
        logger.debug("Get varbit value request -> slot={}, varbitId={}", slot, varbitId);
        Inventory inventory = getInventory();
        if (inventory == null) {
            logger.warn("Bank inventory unavailable when retrieving varbit {}", varbitId);
            return Integer.MIN_VALUE;
        }

        var value = inventory.getVarbitValue(slot, varbitId);
        logger.debug("Get varbit value result -> value={}", value);
        return value;
    }

    public static boolean setTransferOption(TransferOptionType transferoptionType) {
        var depositOptionState = VarDomain.getVarBitValue(WITHDRAW_TYPE_VARBIT_ID);
        logger.debug("Transfer option request -> current={}, target={}", depositOptionState, transferoptionType);
        if (depositOptionState == transferoptionType.getVarbitStateValue()) {
            logger.debug("Transfer option already configured -> {}", transferoptionType);
            return true;
        }
        var result = MiniMenu.doAction(Action.COMPONENT, 1, -1, 33882215) > 0;
        logger.debug("Transfer option change result -> success={}, target={}", result, transferoptionType);
        return result;
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
