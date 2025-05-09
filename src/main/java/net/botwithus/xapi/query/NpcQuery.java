package net.botwithus.xapi.query;

import net.botwithus.rs3.entities.PathingEntity;
import net.botwithus.rs3.world.World;
import net.botwithus.xapi.query.base.PathingEntityQuery;
import net.botwithus.xapi.query.result.EntityResultSet;
import net.botwithus.xapi.query.result.ResultSet;

import java.util.Iterator;

public class NpcQuery extends PathingEntityQuery<PathingEntity> {

    /**
     * Creates a new NpcQuery instance.
     *
     * @return a new NpcQuery instance
     */
    public static NpcQuery newQuery() {
        return new NpcQuery();
    }

    /**
     * Retrieves the results of the query.
     *
     * @return a ResultSet containing the query results
     */
    @Override
    public EntityResultSet<PathingEntity> results() {
        return new EntityResultSet<PathingEntity>(World.getNpcs().stream().filter(this).toList());
    }

    /**
     * Returns an iterator over the query results.
     *
     * @return an Iterator over the query results
     */
    @Override
    public Iterator<PathingEntity> iterator() {
        return results().iterator();
    }

    /**
     * Tests if a pathing entity matches the query predicate.
     *
     * @param pathingEntity the pathing entity to test
     * @return true if the pathing entity matches, false otherwise
     */
    @Override
    public boolean test(PathingEntity pathingEntity) {
        return this.root.test(pathingEntity);
    }

}