package net.botwithus.xapi.game.traversal;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.traversal.enums.LodestoneType;
import net.botwithus.xapi.query.ComponentQuery;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
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

    public static boolean isAvailable(LodestoneType type) {
        int result = VarDomain.getVarBitValue(type.getVarbitId());
        if (logger.isDebugEnabled()) {
            logger.debug("Availability check for {} returned {} (varbit={})", type, result, type.getVarbitId());
        }
        return switch (type) {
            case LUNAR_ISLE -> result >= 100;
            case BANDIT_CAMP -> result >= 15;
            default -> result == 1;
        };
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
     * Teleports the player using the specified Lodestone.
     *
     * @param script the executing script instance controlling delays
     * @param type the Lodestone to teleport to
     * @return {@code true} if the teleport was initiated, {@code false} otherwise
     */
    public static boolean teleport(PermissiveScript script, LodestoneType type) {
        logger.info("Attempting to teleport using {}", type);
        var player = LocalPlayer.self();
        if (player == null) {
            logger.warn("Cannot teleport via {} because the local player is null", type);
            return false;
        }

        script.delay(Rand.nextInt(4500, 6500));

        if (!isOpen()) {
            logger.debug("Lodestone network interface is closed. Opening before teleporting via {}", type);
            if (!open()) {
                logger.warn("Failed to open the Lodestone network interface for {}", type);
                return false;
            }
            if (!isOpen()) {
                int waitTicks = Rand.nextInt(18, 26);
                logger.debug("Waiting up to {} ticks for the Lodestone network interface to open for {}", waitTicks, type);
                script.delayUntil(LodestoneNetwork::isOpen, waitTicks);
                if (!isOpen()) {
                    return false;
                }
            }
        }

        logger.debug("Lodestone network interface open; locating component for {}", type);
        int interfaceId = LodestoneType.getInterfaceId();
        var component = Interfaces.getComponent(interfaceId, type.getComponentId());
        if (component == null) {
            logger.debug(
                "Teleport component for {} not yet available (interfaceId={}, componentId={}); delaying before retrying",
                type,
                interfaceId,
                type.getComponentId()
            );
            script.delay(Rand.nextInt(8, 12));
            return false;
        }

        int interactionResult = component.interact("Teleport");
        var teleportAction = type.getTeleportAction();
        if (interactionResult <= 0 && teleportAction != null) {
            interactionResult = component.interact(teleportAction);
        }
        if (interactionResult <= 0) {
            interactionResult = component.interact();
        }
        if (interactionResult <= 0) {
            logger.warn("Failed to interact with {} (interaction result: {}). Retrying after short delay.", type, interactionResult);
            script.delay(Rand.nextInt(10, 16));
            return false;
        }

        int wax = VarDomain.getVarBitValue(28623);
        int quick = VarDomain.getVarBitValue(28622);
        if (logger.isDebugEnabled()) {
            logger.debug("Teleport interaction succeeded for {} (quick={}, wax={})", type, quick, wax);
        }
        if (quick == 1 && wax > 0) {
            script.delay(Rand.nextInt(4500, 6500));
        } else {
            script.delay(Rand.nextInt(12000, 14000));
        }
        logger.info("Teleport initiated for {}", type);
        return true;
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
