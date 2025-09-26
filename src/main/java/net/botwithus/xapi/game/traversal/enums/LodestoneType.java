package net.botwithus.xapi.game.traversal.enums;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.traversal.LodestoneNetwork;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum LodestoneType {
    AL_KHARID(11, 28, "Al Kharid"),
    ANACHRONIA(25, 44270, "Anachronia"),
    ARDOUGNE(12, 29, "Ardougne"),
    ASHDALE(34, 22430, "Ashdale"),
    BANDIT_CAMP(9, 9482, "Bandit Camp"),
    BURTHORPE(13, 30, "Burthorpe"),
    CANIFIS(27, 18523, "Canifis"),
    CATHERBY(14, 31, "Catherby"),
    CITY_OF_UM(36, 53270, "City of Um"),
    DRAYNOR_VILLAGE(15, 32, "Draynor Village"),
    EDGEVILLE(16, 33, "Edgeville"),
    EAGLES_PEAK(28, 18524, "Eagles' Peak"),
    FALADOR(17, 34, "Falador"),
    FORT_FORINTHRY(23, 52518, "Fort Forinthry"),
    FREMENNIK_PROVINCE(29, 18525, "Fremennik Province"),
    KARAMJA(30, 18526, "Karamja"),
    LUMBRIDGE(18, 35, "Lumbridge"),
    LUNAR_ISLE(10, 9482, "Lunar Isle"),
    MENAPHOS(24, 36173, "Menaphos"),
    OOGLOG(31, 18527, "Oo'glog"),
    PORT_SARIM(19, 36, "Port Sarim"),
    PRIFDDINAS(35, 24967, "Prifddinas"),
    SEERS_VILLAGE(20, 37, "Seers' Village"),
    TAVERLY(21, 38, "Taverley"),
    TIRANNWN(32, 18528, "Tirannwn"),
    VARROCK(22, 39, "Varrock"),
    WILDERNESS_CRATER(33, 18529, "Wilderness Crater"),
    YANILLE(26, 40, "Yanille");

    private static final Logger logger = LoggerFactory.getLogger(LodestoneType.class);
    private static final int INTERFACE_ID = 1092;

    public static int getInterfaceId() {
        return INTERFACE_ID;
    }

    private final int componentId;
    private final int varbitId;
    private final String teleportAction;

    LodestoneType(int componentId, int varbitId, String teleportTarget) {
        this.componentId = componentId;
        this.varbitId = varbitId;
        if (teleportTarget == null || teleportTarget.isBlank()) {
            this.teleportAction = null;
        } else {
            this.teleportAction = "Teleport " + teleportTarget;
        }
    }

    public int getComponentId() {
        return componentId;
    }

    public int getVarbitId() {
        return varbitId;
    }

    public String getTeleportAction() {
        return teleportAction;
    }

    public boolean isAvailable() {
        int result = VarDomain.getVarBitValue(varbitId);
        if (logger.isDebugEnabled()) {
            logger.debug("Availability check for {} returned {} (varbit={})", this, result, varbitId);
        }
        switch (this) {
            case LUNAR_ISLE -> {
                return result >= 100;
            }
            case BANDIT_CAMP -> {
                return result >= 15;
            }
        }
        return result == 1;
    }

    @Deprecated(forRemoval = false)
    public boolean isAvaialable() {
        return isAvailable();
    }

    public boolean teleport(PermissiveScript script) {
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
                if (!LodestoneNetwork.isOpen()) {
                    return false;
                }
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

