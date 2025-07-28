package net.botwithus.xapi.game.traversal;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;
import net.botwithus.util.Rand;
import net.botwithus.xapi.util.Logger;

public class Traverse {

    private static final int MAX_LOCAL_DISTANCE = 80;
    private static final int MAX_STEP_SIZE = 16;
    private static final int MIN_STEP_SIZE = 10;

    /**
     * Walks to a coordinate, automatically choosing minimap usage and step size.
     * If the distance to the destination is less than 24, does not use minimap; otherwise, uses minimap.
     * Step size is randomized between 10 and 16 (inclusive).
     * @param destinationCoord The destination coordinate
     * @return true if walking was initiated successfully
     */
    public static boolean to(Coordinate destinationCoord) {
        if (destinationCoord == null) {
            Logger.logWarn("ERROR: Coordinate is null");
            return false;
        }
        var distance = Distance.to(destinationCoord);
        var useMinimap = distance >= Rand.nextInt(22, 28);
        var stepSize = Rand.nextInt(MIN_STEP_SIZE, MAX_STEP_SIZE + 1);
        return bresenhamTo(destinationCoord, useMinimap, stepSize);
    }
    
    /**
     * Walks to a coordinate using Bresenham line algorithm for pathfinding
     * @param destinationCoord The destination coordinate
     * @param minimap Whether to use minimap for walking (currently ignored, uses MiniMenu)
     * @param stepSize Maximum step size for each movement
     * @return true if walking was initiated successfully
     */
    public static boolean bresenhamTo(Coordinate destinationCoord, boolean minimap, int stepSize) {
        LocalPlayer player = LocalPlayer.self();
        if (player == null) {
            Logger.logWarn("[Traverse#bresenham] Player is null");
            return false;
        }

        Coordinate currentCoordinate = player.getCoordinate();
        if (currentCoordinate == null) {
            Logger.logWarn("[Traverse#bresenham] Current coordinate is null");
            return false;
        }

        int dx = destinationCoord.x() - currentCoordinate.x();
        int dy = destinationCoord.y() - currentCoordinate.y();
        int distance = (int)Math.hypot(dx, dy);

        if (distance > stepSize) {
            int stepX = destinationCoord.x() + dx * stepSize / distance;
            int stepY = destinationCoord.y() + dy * stepSize / distance;
            return walkTo(new Coordinate(stepX, stepY, destinationCoord.z()), minimap);
        } else {
            return walkTo(destinationCoord, minimap);
        }
    }

    /**
     * Walks to a coordinate using MiniMenu
     * @param destinationCoord The destination coordinate
     * @param minimap Whether to use minimap for walking (currently ignored, uses MiniMenu)
     * @return true if walking was initiated successfully
     */
    public static boolean walkTo(Coordinate destinationCoord, boolean minimap) {
        if (destinationCoord == null) {
            Logger.logWarn("ERROR: Coordinate is null");
            return false;
        }

        try {
            Logger.logInfo("Attempting to walk to " + destinationCoord.x() + ", " + destinationCoord.y());

            if (Distance.to(destinationCoord) < 2) {
                Logger.logInfo("Already close to target location, skipping walk");
                return true;
            }

            if (Distance.to(destinationCoord) > MAX_LOCAL_DISTANCE) {
                Logger.logInfo("Target location is too far away, using Bresenham pathfinding");
                return bresenhamTo(destinationCoord, minimap, Rand.nextInt(MIN_STEP_SIZE, MAX_STEP_SIZE));
            }

            int result = MiniMenu.doAction(Action.WALK, minimap ? 1 : 0, destinationCoord.x(), destinationCoord.y());

            if (result > 0) {
                Logger.logInfo("Successfully initiated walk to " + destinationCoord.x() + ", " + destinationCoord.y());
                return true;
            } else {
                Logger.logWarn("Failed to walk to " + destinationCoord.x() + ", " + destinationCoord.y() + " - result: " + result);
                return false;
            }
        } catch (Exception e) {
            Logger.logTrace("Exception while walking to " + destinationCoord.x() + ", " + destinationCoord.y() + ": " + e.getMessage(), e);
            return false;
        }
    }
}
