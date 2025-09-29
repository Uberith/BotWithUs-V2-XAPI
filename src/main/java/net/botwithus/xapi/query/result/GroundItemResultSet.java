package net.botwithus.xapi.query.result;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.item.GroundItem;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class GroundItemResultSet extends ResultSet<GroundItem> {

    public GroundItemResultSet(List<GroundItem> results) {
        super(results);
    }

    public GroundItem nearestWithin(double maxDistance) {
        var player = LocalPlayer.self();
        if (player == null) {
            return null;
        }
        return nearestWithin(player.getCoordinate(), maxDistance);
    }

    public GroundItem nearestWithin(Coordinate coordinate, double maxDistance) {
        if (coordinate == null) {
            return null;
        }
        return results.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getStack() != null && item.getStack().getCoordinate() != null)
                .filter(item -> Distance.between(item.getStack().getCoordinate(), coordinate) <= maxDistance)
                .min(Comparator.comparingDouble(item -> Distance.between(item.getStack().getCoordinate(), coordinate)))
                .orElse(null);
    }
}
