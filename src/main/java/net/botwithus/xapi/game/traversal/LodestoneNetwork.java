package net.botwithus.xapi.game.traversal;

import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.xapi.query.ComponentQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LodestoneNetwork {
    private static final Logger logger = LoggerFactory.getLogger(LodestoneNetwork.class);

    public static boolean isOpen() {
        var open = Interfaces.isOpen(1092);
        if (logger.isDebugEnabled()) {
            logger.debug("Lodestone network interface {}", open ? "is open" : "is not open");
        }
        return open;
    }

    /**
     * Opens the Lodestone Network interface.
     *
     * @return {@code true} if the interface was opened, {@code false} otherwise.
     */
    public static boolean open() {
        logger.info("Attempting to open the Lodestone network interface");
        var result = ComponentQuery.newQuery(1465).option("Lodestone network").results().first();
        if (result == null) {
            logger.warn("Unable to locate the Lodestone network component");
            return false;
        }

        var interactionResult = result.interact("Lodestone network");
        if (interactionResult > 0) {
            logger.info("Successfully opened the Lodestone network interface");
            return true;
        }

        logger.warn("Failed to open the Lodestone network interface (interaction result: {})", interactionResult);
        return false;
    }

    /**
     * Teleports the player to their previous destination.
     *
     * @return true if the player was successfully teleported, false otherwise
     */
    public static boolean teleportToPreviousDestination() {
        logger.info("Attempting to teleport to the previous destination");
        var result = ComponentQuery.newQuery(1465).option("Previous Destination").results().first();
        if (result == null) {
            logger.warn("Unable to locate the Previous Destination component");
            return false;
        }

        var interactionResult = result.interact("Previous Destination");
        if (interactionResult > 0) {
            logger.info("Teleport to the previous destination initiated");
            return true;
        }

        logger.warn("Failed to teleport to the previous destination (interaction result: {})", interactionResult);
        return false;
    }
}
