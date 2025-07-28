package net.botwithus.xapi.query.base;

import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.PathingEntity;

import java.util.Arrays;

public abstract class PathingEntityQuery<T extends PathingEntity> extends EntityQuery<T> {

    /**
     * Filters pathing entities by index.
     *
     * @param indices the indices to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> index(int... indices) {
        if (indices.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(indices).anyMatch(i -> t.getIndex() == i));
        return this;
    }

    /**
     * Filters pathing entities by type ID.
     *
     * @param typeIds the type IDs to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> typeId(int... typeIds) {
        if (typeIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(typeIds).anyMatch(i -> t.getTypeId() == i));
        return this;
    }

    /**
     * Filters pathing entities by name.
     *
     * @param names the names to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> name(String... names) {
        if (names.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(names).anyMatch(n -> t.getName().equals(n)));
        return this;
    }

    public PathingEntityQuery<T> name(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            String entityName = t.getName();
            return entityName != null && Arrays.stream(patterns).anyMatch(p -> p.matcher(entityName).matches());
        });
        return this;
    }

    /**
     * Filters pathing entities by overhead text.
     *
     * @param overheadTexts the overhead texts to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> overheadText(String... overheadTexts) {
        if (overheadTexts.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(overheadTexts).anyMatch(n -> t.getOverheadText().equals(n)));
        return this;
    }

    /**
     * Filters pathing entities by moving status.
     *
     * @param isMoving the moving status to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> isMoving(boolean isMoving) {
        this.root = this.root.and(t -> t.isMoving() == isMoving);
        return this;
    }

    /**
     * Filters pathing entities by animation ID.
     *
     * @param animationIds the animation IDs to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> animationId(int... animationIds) {
        if (animationIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(animationIds).anyMatch(i -> t.getAnimationId() == i));
        return this;
    }

    /**
     * Filters pathing entities by stance ID.
     *
     * @param stanceIds the stance IDs to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> stanceId(int... stanceIds) {
        if (stanceIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(stanceIds).anyMatch(i -> t.getStanceId() == i));
        return this;
    }

    /**
     * Filters pathing entities by health range.
     *
     * @param min the minimum health
     * @param max the maximum health
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> health(int min, int max) {
        this.root = this.root.and(t -> t.getHealth() >= min && t.getHealth() <= max);
        return this;
    }

    /**
     * Filters pathing entities by following entity type and index.
     *
     * @param type the entity type to filter by
     * @param index the entity index to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> following(EntityType type, int index) {
        this.root = this.root.and(t -> t.getFollowingType() == type && t.getFollowingIndex() == index);
        return this;
    }

    /**
     * Filters pathing entities by following specific entities.
     *
     * @param entity the entities to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> following(PathingEntity... entity) {
        this.root = this.root.and(t -> Arrays.stream(entity).anyMatch(e -> t.getFollowingType() == e.getType() && t.getFollowingIndex() == e.getIndex()));
        return this;
    }

    /**
     * Filters pathing entities by headbars.
     *
     * @param headbars the headbars to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> headbars(int... headbars) {
        if (headbars.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(headbars).anyMatch(i -> t.getHeadbar(i) != null));
        return this;
    }

    /**
     * Filters pathing entities by hitmarks.
     *
     * @param hitmarks the hitmarks to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> hitmarks(int... hitmarks) {
        if (hitmarks.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(hitmarks).anyMatch(i -> t.getHitmark(i) != null));
        return this;
    }

    /**
     * Filters pathing entities by options.
     *
     * @param options the options to filter by
     * @return the updated PathingEntityQuery
     */
    public PathingEntityQuery<T> option(String... options) {
        if (options.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(options).anyMatch(o -> t.getOptions().contains(o)));
        return this;
    }
    

    public PathingEntityQuery<T> option(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var options = t.getOptions();
            return options != null && options.stream().anyMatch(opt ->
                Arrays.stream(patterns).anyMatch(p -> p.matcher(opt).matches())
            );
        });
        return this;
    }
}