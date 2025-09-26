package net.botwithus.xapi.query.base;

import net.botwithus.rs3.entities.EntityType;
import net.botwithus.rs3.entities.PathingEntity;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

public abstract class PathingEntityQuery<T extends PathingEntity> extends EntityQuery<T> {

    public PathingEntityQuery<T> index(int... indices) {
        if (indices.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(indices).anyMatch(i -> t.getIndex() == i));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> typeId(int... typeIds) {
        if (typeIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(typeIds).anyMatch(i -> t.getTypeId() == i));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> name(String... names) {
        if (names.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(names).anyMatch(n -> n != null && n.equals(t.getName())));
        predicateChanged();
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
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> overheadText(String... overheadTexts) {
        if (overheadTexts.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(overheadTexts)
                .anyMatch(text -> text != null && text.equals(t.getOverheadText())));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> isMoving(boolean isMoving) {
        this.root = this.root.and(t -> t.isMoving() == isMoving);
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> animationId(int... animationIds) {
        if (animationIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(animationIds).anyMatch(i -> t.getAnimationId() == i));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> stanceId(int... stanceIds) {
        if (stanceIds.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(stanceIds).anyMatch(i -> t.getStanceId() == i));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> health(int min, int max) {
        this.root = this.root.and(t -> t.getHealth() >= min && t.getHealth() <= max);
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> following(EntityType type, int index) {
        this.root = this.root.and(t -> t.getFollowingType() == type && t.getFollowingIndex() == index);
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> following(PathingEntity... entity) {
        if (entity.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(entity)
                .filter(Objects::nonNull)
                .anyMatch(e -> t.getFollowingType() == e.getType() && t.getFollowingIndex() == e.getIndex()));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> headbars(int... headbars) {
        if (headbars.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(headbars).anyMatch(i -> t.getHeadbar(i) != null));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> hitmarks(int... hitmarks) {
        if (hitmarks.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> Arrays.stream(hitmarks).anyMatch(i -> t.getHitmark(i) != null));
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> option(BiFunction<String, CharSequence, Boolean> spred, String... option) {
        if (option.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var options = t.getOptions();
            return options != null && Arrays.stream(option)
                    .filter(Objects::nonNull)
                    .anyMatch(i -> options.stream().anyMatch(j -> j != null && spred.apply(i, j)));
        });
        predicateChanged();
        return this;
    }

    public PathingEntityQuery<T> option(String... option) {
        return option(String::contentEquals, option);
    }

    public PathingEntityQuery<T> option(java.util.regex.Pattern... patterns) {
        if (patterns.length == 0) {
            return this;
        }
        this.root = this.root.and(t -> {
            var options = t.getOptions();
            return options != null && options.stream().anyMatch(opt ->
                    Arrays.stream(patterns).anyMatch(p -> p.matcher(opt).matches()));
        });
        predicateChanged();
        return this;
    }
}
