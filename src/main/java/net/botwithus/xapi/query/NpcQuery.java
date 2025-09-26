package net.botwithus.xapi.query;

import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.world.World;
import net.botwithus.xapi.query.base.PathingEntityQuery;
import net.botwithus.xapi.query.base.QueryCache;
import net.botwithus.xapi.query.result.EntityResultSet;

import java.time.Duration;
import java.util.Iterator;

public class NpcQuery extends PathingEntityQuery<PathingEntity> {

    private final QueryCache<EntityResultSet<PathingEntity>> cache = new QueryCache<>();

    public static NpcQuery newQuery() {
        return new NpcQuery();
    }

    public static NpcQuery hostileWithin(double distance, String... names) {
        return newQuery().name(names).distance(distance).valid(true);
    }

    public NpcQuery withCache(Duration ttl) {
        cache.configure(ttl);
        return this;
    }

    @Override
    public EntityResultSet<PathingEntity> results() {
        return cache.getOrCompute(() -> new EntityResultSet<>(World.getNpcs().stream().filter(this).toList()));
    }

    @Override
    public Iterator<PathingEntity> iterator() {
        return results().iterator();
    }

    @Override
    public boolean test(PathingEntity pathingEntity) {
        return this.root.test(pathingEntity);
    }

    @Override
    protected void predicateChanged() {
        cache.invalidate();
        super.predicateChanged();
    }
}
