package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.ConfigManager;
import net.botwithus.rs3.interfaces.Component;
import net.botwithus.rs3.interfaces.ComponentType;
import net.botwithus.rs3.interfaces.InterfaceManager;
import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.xapi.query.base.Query;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.ResultSet;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ComponentQuery implements Query<Component, ResultSet<Component>> {

    protected Predicate<Component> root;
    private final QueryCache<ResultSet<Component>> cache = new QueryCache<>();
    private int[] ids;

    /**
     * Constructs a new ComponentQuery with the specified IDs.
     *
     * @param ids the IDs to query
     */
    public ComponentQuery(int... ids) {
        this.ids = ids;
        root = t -> Arrays.stream(ids).anyMatch(i -> i == t.getRoot().getInterfaceId());
    }

    /**
     * Creates a new ComponentQuery with the specified IDs.
     *
     * @param ids the IDs to query
     * @return a new ComponentQuery instance
     */
    public static ComponentQuery newQuery(int... ids) {
        return new ComponentQuery(ids);
    }

    public ComponentQuery withCache(Duration ttl) {
        cache.configure(ttl);
        return this;
    }

    public static ComponentQuery visible(int... interfaceIds) {
        return newQuery(interfaceIds).hidden(false);
    }

    public static ComponentQuery buttonWithText(int interfaceId, int componentId, String... text) {
        return newQuery(interfaceId)
                .id(componentId)
                .hidden(false)
                .type(ComponentType.BUTTON)
                .text((needle, haystack) -> haystack != null && needle.equalsIgnoreCase(haystack.toString()), text);
    }

    /**
     * Filters components by type.
     *
     * @param type the types to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery type(ComponentType... type) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(type).anyMatch(i -> t.getType() == i));
        return this;
    }

    /**
     * Filters components by ID.
     *
     * @param ids the IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery id(int... ids) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(ids).anyMatch(i -> i == t.getComponentId()));
        return this;
    }

    /**
     * Filters components by subcomponent ID.
     *
     * @param ids the subcomponent IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery subComponentId(int... ids) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(ids).anyMatch(i -> i == t.getSubComponentId()));
        return this;
    }

    /**
     * Filters components by hidden status.
     *
     * @param hidden the hidden status to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery hidden(boolean hidden) {
        invalidateCache();

        this.root = this.root.and(t -> t.isHidden() == hidden);
        return this;
    }

    /**
     * Filters components by properties.
     *
     * @param properties the properties to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery properties(int... properties) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(properties).anyMatch(i -> i == t.getProperties()));
        return this;
    }

    /**
     * Filters components by font ID.
     *
     * @param fontIds the font IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery fontId(int... fontIds) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(fontIds).anyMatch(i -> i == t.getFontId()));
        return this;
    }

    /**
     * Filters components by color.
     *
     * @param colors the colors to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery color(int... colors) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(colors).anyMatch(i -> i == t.getColor()));
        return this;
    }

    /**
     * Filters components by alpha.
     *
     * @param alphas the alphas to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery alpha(int... alphas) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(alphas).anyMatch(i -> i == t.getAlpha()));
        return this;
    }

    /**
     * Filters components by item ID.
     *
     * @param itemIds the item IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery itemId(int... itemIds) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(itemIds).anyMatch(i -> i == t.getItemId()));
        return this;
    }

    /**
     * Filters components by item name using a custom string predicate.
     *
     * @param name the item name to filter by
     * @param spred the predicate to match the item name
     * @return the updated ComponentQuery
     */
    public ComponentQuery itemName(String name, BiFunction<String, CharSequence, Boolean> spred) {
        invalidateCache();

        this.root = this.root.and(t -> {
            var itemName = ConfigManager.getItemProvider().provide(t.getItemId()).getName();
            return spred.apply(name, itemName);
        });
        return this;
    }

    /**
     * Filters components by item name.
     *
     * @param name the item name to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery itemName(String name) {
        return itemName(name, String::contentEquals);
    }

    /**
     * Filters components by item amount.
     *
     * @param amounts the item amounts to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery itemAmount(int... amounts) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(amounts).anyMatch(i -> i == t.getItemAmount()));
        return this;
    }

    /**
     * Filters components by sprite ID.
     *
     * @param spriteIds the sprite IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery spriteId(int... spriteIds) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(spriteIds).anyMatch(i -> i == t.getSpriteId()));
        return this;
    }

    /**
     * Filters components by text.
     *
     * @param spred the predicate to match text
     * @param text the text to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery text(BiFunction<String, CharSequence, Boolean> spred, String... text) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(text).anyMatch(i -> spred.apply(i, t.getText())));
        return this;
    }

    /**
     * Filters components by option-based text.
     *
     * @param spred the predicate to match option-based text
     * @param text the option-based text to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery optionBasedText(BiFunction<String, CharSequence, Boolean> spred, String... text) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(text).anyMatch(i -> spred.apply(i, t.getOptionBase())));
        return this;
    }

    /**
     * Filters components by options.
     *
     * @param spred the predicate to match options
     * @param option the options to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery option(BiFunction<String, CharSequence, Boolean> spred, String... option) {
        invalidateCache();

        this.root = this.root.and(t -> {
            var options = t.getOptions();
            return options != null && Arrays.stream(option).anyMatch(i -> i != null && options.stream().anyMatch(j -> j != null && spred.apply(i, j)));
        });
        return this;
    }

    /**
     * Filters components by options using content equality.
     *
     * @param option the options to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery option(String... option) {
        return option(String::contentEquals, option);
    }

    /**
     * Filters components by parameters.
     *
     * @param params the parameters to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery params(int... params) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(params).anyMatch(i -> t.getParams().containsKey(i)));
        return this;
    }

    /**
     * Filters components by children IDs.
     *
     * @param ids the children IDs to filter by
     * @return the updated ComponentQuery
     */
    public ComponentQuery children(int... ids) {
        invalidateCache();

        this.root = this.root.and(t -> Arrays.stream(ids).anyMatch(i -> t.getChildren().stream().anyMatch(j -> j.getComponentId() == i)));
        return this;
    }

    /**
     * Retrieves the results of the query.
     *
     * @return a ResultSet containing the query results
     */
    @Override
    public ResultSet<Component> results() {
        return cache.getOrCompute(() -> new ResultSet<>(
                Arrays.stream(ids)
                        .mapToObj(Interfaces::getInterface) // Map IDs to Interfaces
                        .filter(Objects::nonNull) // Filter out null interfaces
                        .flatMap(interfaceManager -> interfaceManager.getComponents().stream()) // Flatten components
                        .filter(this) // Apply the predicate (root.test)
                        .toList() // Collect the filtered components into a list
        ));
    }

    /**
     * Returns an iterator over the query results.
     *
     * @return an Iterator over the query results
     */
    @Override
    public Iterator<Component> iterator() {
        return results().iterator();
    }

    /**
     * Tests if a component matches the query predicate.
     *
     * @param comp the component to test
     * @return true if the component matches, false otherwise
     */
    @Override
    public boolean test(Component comp) {
        return this.root.test(comp);
    }

    private void invalidateCache() {
        cache.invalidate();
    }

}