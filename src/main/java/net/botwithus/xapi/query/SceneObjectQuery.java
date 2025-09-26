package net.botwithus.xapi.query;

import net.botwithus.rs3.cache.assets.so.SceneObjectDefinition;
import net.botwithus.rs3.entities.SceneObject;
import net.botwithus.rs3.world.World;
import net.botwithus.xapi.query.base.EntityQuery;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.EntityResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;

public class SceneObjectQuery extends EntityQuery<SceneObject> {

    private static final Logger logger = LoggerFactory.getLogger(SceneObjectQuery.class);
    private final QueryCache<EntityResultSet<SceneObject>> cache = new QueryCache<>();

    public static SceneObjectQuery newQuery() {
        return new SceneObjectQuery();
    }

    public static SceneObjectQuery interactable(int... typeIds) {
        return newQuery().typeId(typeIds).hidden(false);
    }

    public SceneObjectQuery withCache(Duration ttl) {
        cache.configure(ttl);
        return this;
    }

    @Override
    public EntityResultSet<SceneObject> results() {
        return cache.getOrCompute(() -> new EntityResultSet<>(World.getSceneObjects().stream().filter(this).toList()));
    }

    @Override
    public Iterator<SceneObject> iterator() {
        return results().iterator();
    }

    @Override
    public boolean test(SceneObject sceneObject) {
        return this.root.test(sceneObject);
    }

    public SceneObjectQuery typeId(int... typeIds) {
        if (typeIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(typeIds).anyMatch(i -> t.getTypeId() == i));
        predicateChanged();
        return this;
    }

    public SceneObjectQuery animation(int... animations) {
        if (animations.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(animations).anyMatch(i -> t.getAnimationId() == i));
        predicateChanged();
        return this;
    }

    public SceneObjectQuery hidden(boolean hidden) {
        this.root = this.root.and(t -> t.isHidden() == hidden);
        predicateChanged();
        return this;
    }

    public SceneObjectQuery multiType(SceneObjectDefinition... sceneObjectDefinitions) {
        if (sceneObjectDefinitions.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(sceneObjectDefinitions).anyMatch(i -> t.getMultiType() == i));
        predicateChanged();
        return this;
    }

    public SceneObjectQuery name(BiFunction<String, CharSequence, Boolean> spred, String... names) {
        if (names.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(names)
                .filter(Objects::nonNull)
                .anyMatch(i -> spred.apply(i, t.getName())));
        predicateChanged();
        return this;
    }

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
        predicateChanged();
        return this;
    }

    public SceneObjectQuery option(BiFunction<String, CharSequence, Boolean> spred, String... options) {
        if (options.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var objOptions = t.getOptions();
            return objOptions != null && Arrays.stream(options)
                    .filter(Objects::nonNull)
                    .anyMatch(i -> objOptions.stream().anyMatch(j -> j != null && spred.apply(i, j)));
        });
        predicateChanged();
        return this;
    }

    public SceneObjectQuery option(String... option) {
        return option(String::contentEquals, option);
    }

    public SceneObjectQuery option(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var objOptions = t.getOptions();
            return objOptions != null && objOptions.stream().anyMatch(opt ->
                    Arrays.stream(patterns).anyMatch(p -> p.matcher(opt).matches()));
        });
        predicateChanged();
        return this;
    }

    @Override
    protected void predicateChanged() {
        cache.invalidate();
        super.predicateChanged();
    }
}
