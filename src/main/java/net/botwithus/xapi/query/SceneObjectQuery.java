package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.so.SceneObjectDefinition;
import net.botwithus.rs3.entities.SceneObject;
import net.botwithus.rs3.world.World;
import net.botwithus.xapi.query.base.EntityQuery;
import net.botwithus.xapi.query.result.EntityResultSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiFunction;

public class SceneObjectQuery extends EntityQuery<SceneObject> {

    /**
     * Creates a new SceneObjectQuery instance.
     *
     * @return a new SceneObjectQuery instance
     */
    public static SceneObjectQuery newQuery() {
        return new SceneObjectQuery();
    }

    /**
     * Retrieves the results of the query.
     *
     * @return a ResultSet containing the query results
     */
    @Override
    public EntityResultSet<SceneObject> results() {
        return new EntityResultSet<>(World.getSceneObjects().stream().filter(this).toList());
    }

    /**
     * Returns an iterator over the query results.
     *
     * @return an Iterator over the query results
     */
    @Override
    public Iterator<SceneObject> iterator() {
        return results().iterator();
    }

    /**
     * Tests if a scene object matches the query predicate.
     *
     * @param sceneObject the scene object to test
     * @return true if the scene object matches, false otherwise
     */
    @Override
    public boolean test(SceneObject sceneObject) {
        return this.root.test(sceneObject);
    }

    /**
     * Filters scene objects by type ID.
     *
     * @param typeIds the type IDs to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery typeId(int... typeIds) {
        if (typeIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(typeIds).anyMatch(i -> t.getTypeId() == i));
        return this;
    }

    /**
     * Filters scene objects by animation ID.
     *
     * @param animations the animation IDs to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery animation(int... animations) {
        if (animations.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(animations).anyMatch(i -> t.getAnimationId() == i));
        return this;
    }

    /**
     * Filters scene objects by hidden status.
     *
     * @param hidden the hidden status to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery hidden(boolean hidden) {
        this.root = this.root.and(t -> t.isHidden() == hidden);
        return this;
    }

    /**
     * Filters scene objects by multiple types.
     *
     * @param sceneObjectDefinitions the location types to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery multiType(SceneObjectDefinition... sceneObjectDefinitions) {
        if (sceneObjectDefinitions.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(sceneObjectDefinitions).anyMatch(i -> t.getMultiType() == i));
        return this;
    }

    /**
     * Filters scene objects by name using a predicate.
     *
     * @param spred the predicate to match names
     * @param names the names to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(names).anyMatch(i -> spred.apply(i, t.getName())));
        return this;
    }

    /**
     * Filters scene objects by name using content equality.
     *
     * @param names the names to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery name(String... names) {
        return name(String::contentEquals, names);
    }

    public SceneObjectQuery name(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            String objName = t.getName();
            return objName != null && Arrays.stream(patterns).anyMatch(p -> p.matcher(objName).matches());
        });
        return this;
    }

    /**
     * Filters scene objects by options using a predicate.
     *
     * @param spred the predicate to match options
     * @param options the options to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery option(BiFunction<String, CharSequence, Boolean> spred, String... options) {
        if (options.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var objOptions = t.getOptions();
            return objOptions != null && Arrays.stream(options).anyMatch(i -> objOptions.stream().anyMatch(j -> spred.apply(i, j)));
        });
        return this;
    }

    /**
     * Filters scene objects by options using content equality.
     *
     * @param option the options to filter by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery option(String... option) {
        return option(String::contentEquals, option);
    }

    /**
     * Filters scene objects by options using regular expression patterns.
     *
     * @param patterns the regex patterns to filter options by
     * @return the updated SceneObjectQuery
     */
    public SceneObjectQuery option(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var objOptions = t.getOptions();
            return objOptions != null && objOptions.stream().anyMatch(opt ->
                Arrays.stream(patterns).anyMatch(p -> p.matcher(opt).matches())
            );
        });
        return this;
    }
}