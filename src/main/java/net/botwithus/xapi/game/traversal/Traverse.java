package net.botwithus.xapi.game.traversal;

import botwithus.navigation.api.NavPath;
import botwithus.navigation.api.State;
import botwithus.navigation.api.TeleportData;
import net.botwithus.rs3.world.Coordinate;
import net.botwithus.rs3.world.Distance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Traverse {
    private static final Logger logger = LoggerFactory.getLogger(Traverse.class);

    private Traverse() {
        // Utility class
    }

    /**
     * Resolve and process a navigation path using the BotWithUs navigation API.
     *
     * @param destinationCoord destination tile to reach.
     * @return {@code true} if navigation could be started or destination already reached.
     */
    public static boolean to(Coordinate destinationCoord) {
        return navigate(destinationCoord);
    }

    /**
     * Backwards compatible entry point that now delegates to the navigation API.
     * The {@code minimap} and {@code stepSize} hints are ignored because the nav-api
     * is responsible for optimising the route.
     */
    public static boolean bresenhamTo(Coordinate destinationCoord, boolean minimap, int stepSize) {
        return navigate(destinationCoord);
    }

    /**
     * Backwards compatible entry point that now delegates to the navigation API.
     * The {@code minimap} hint is ignored because the nav-api determines the optimal route.
     */
    public static boolean walkTo(Coordinate destinationCoord, boolean minimap) {
        return navigate(destinationCoord);
    }

    private static boolean navigate(Coordinate destinationCoord) {
        if (destinationCoord == null) {
            logger.warn("Cannot traverse: destination coordinate is null");
            return false;
        }

        try {
            if (Distance.to(destinationCoord) < 1.5d) {
                logger.debug("Already adjacent to {}, skipping navigation.", destinationCoord);
                return true;
            }
        } catch (Exception distanceError) {
            logger.trace("Distance check failed before navigation: {}", distanceError.getMessage(), distanceError);
        }

        try {
            NavPath path = NavPath.resolve(destinationCoord);
            if (path == null) {
                logger.warn("Nav API returned no path for {}", destinationCoord);
                return false;
            }

            path.process();
            State state = path.state();

            path.getUsedTeleport().ifPresent(Traverse::logTeleportUsage);

            switch (state) {
                case FINISHED -> {
                    logger.debug("Navigator reports destination {} already reached.", destinationCoord);
                    return true;
                }
                case CONTINUE -> {
                    logger.debug("Navigator processing path to {} (cost {}).", destinationCoord, path.getCost());
                    return true;
                }
                case NO_PATH -> {
                    logger.warn("Navigator could not resolve a path to {}.", destinationCoord);
                    return false;
                }
                case FAILED -> {
                    logger.warn("Navigator failed while processing path to {}.", destinationCoord);
                    return false;
                }
                case IDLE -> {
                    logger.debug("Navigator is idle for destination {}.", destinationCoord);
                    return true;
                }
                default -> {
                    logger.warn("Navigator returned unexpected state {} for {}.", state, destinationCoord);
                    return false;
                }
            }
        } catch (UnsatisfiedLinkError e) {
            logger.error("Nav API native bindings are unavailable while traversing to {}.", destinationCoord, e);
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error while traversing to {}.", destinationCoord, e);
            return false;
        }
    }

    private static void logTeleportUsage(TeleportData teleportData) {
        logger.info(
                "Nav API selected teleport '{}' (type {}) to {}",
                teleportData.optionName(),
                teleportData.interactionType(),
                teleportData.toTile()
        );
    }
}
