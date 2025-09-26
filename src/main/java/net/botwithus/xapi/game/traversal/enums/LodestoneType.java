package net.botwithus.xapi.game.traversal.enums;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.traversal.LodestoneNetwork;
import net.botwithus.xapi.script.base.DelayableScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LodestoneType {
    AL_KHARID(11, "Al Kharid"),
    ANACHRONIA(25, "Anachronia"),
    ARDOUGNE(12, "Ardougne"),
    ASHDALE(34, "Ashdale"),
    BANDIT_CAMP(9, "Bandit Camp"),
    BURTHORPE(13, "Burthorpe"),
    CANIFIS(27, "Canifis"),
    CATHERBY(14, "Catherby"),
    CITY_OF_UM(36, "City of Um"),
    DRAYNOR_VILLAGE(15, "Draynor Village"),
    EDGEVILLE(16, "Edgeville"),
    EAGLES_PEAK(28, "Eagles' Peak"),
    FALADOR(17, "Falador"),
    FORT_FORINTHRY(23, "Fort Forinthry"),
    FREMENNIK_PROVINCE(29, "Fremennik Province"),
    KARAMJA(30, "Karamja"),
    LUMBRIDGE(18, "Lumbridge"),
    LUNAR_ISLE(10, "Lunar Isle"),
    MENAPHOS(24, "Menaphos"),
    OOGLOG(31, "Oo'glog"),
    PORT_SARIM(19, "Port Sarim"),
    PRIFDDINAS(35, "Prifddinas"),
    SEERS_VILLAGE(20, "Seers' Village"),
    TAVERLY(21, "Taverley"),
    TIRANNWN(32, "Tirannwn"),
    VARROCK(22, "Varrock"),
    WILDERNESS_CRATER(33, "Wilderness Crater"),
    YANILLE(26, "Yanille");

    private static final Logger logger = LoggerFactory.getLogger(LodestoneType.class);
    private static final int INTERFACE_ID = 1092;

    private final int componentId;
    private final String teleportAction;

    LodestoneType(int componentId, String teleportTarget) {
        this.componentId = componentId;
        if (teleportTarget == null || teleportTarget.isBlank()) {
            this.teleportAction = null;
        } else {
            this.teleportAction = "Teleport " + teleportTarget;
        }
    }

    public boolean teleport(DelayableScript script) {
        logger.info("Attempting to teleport using {}", this);
        var player = LocalPlayer.self();
        if (player == null) {
            logger.warn("Cannot teleport via {} because the local player is null", this);
            return false;
        }

        script.delay(Rand.nextInt(4500, 6500));

        if (!LodestoneNetwork.isOpen()) {
            logger.debug("Lodestone network interface is closed. Opening before teleporting via {}", this);
            if (!LodestoneNetwork.open()) {
                logger.warn("Failed to open the Lodestone network interface for {}", this);
                return false;
            }
            if (!LodestoneNetwork.isOpen()) {
                int waitTicks = Rand.nextInt(18, 26);
                logger.debug("Waiting up to {} ticks for the Lodestone network interface to open for {}", waitTicks, this);
                script.delayUntil(LodestoneNetwork::isOpen, waitTicks);
                return false;
            }
        }

        logger.debug("Lodestone network interface open; locating component for {}", this);
        var componentId = this.componentId;
        var component = Interfaces.getComponent(INTERFACE_ID, componentId);
        if (component == null) {
            logger.debug("Teleport component for {} not yet available (interfaceId={}, componentId={}); delaying before retrying", this, INTERFACE_ID, componentId);
            script.delay(Rand.nextInt(8, 12));
            return false;
        }

        int interactionResult = component.interact("Teleport");
        if (interactionResult <= 0 && teleportAction != null) {
            interactionResult = component.interact(teleportAction);
        }
        if (interactionResult <= 0) {
            interactionResult = component.interact();
        }
        if (interactionResult <= 0) {
            logger.warn("Failed to interact with {} (interaction result: {}). Retrying after short delay.", this, interactionResult);
            script.delay(Rand.nextInt(10, 16));
            return false;
        }

        int wax = VarDomain.getVarBitValue(28623);
        int quick = VarDomain.getVarBitValue(28622);
        if (logger.isDebugEnabled()) {
            logger.debug("Teleport interaction succeeded for {} (quick={}, wax={})", this, quick, wax);
        }
        if (quick == 1 && wax > 0) {
            script.delay(Rand.nextInt(4500, 6500));
        } else {
            script.delay(Rand.nextInt(12000, 14000));
        }
        logger.info("Teleport initiated for {}", this);
        return true;
    }

}

