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
    AL_KHARID(11, 28),
    ANACHRONIA(25, 44270),
    ARDOUGNE(12, 29),
    ASHDALE(34, 22430),
    BANDIT_CAMP(9, 9482),
    BURTHORPE(13, 30),
    CANIFIS(27, 18523),
    CATHERBY(14, 31),
    CITY_OF_UM(36, 53270),
    DRAYNOR_VILLAGE(15, 32),
    EDGEVILLE(16, 33),
    EAGLES_PEAK(28, 18524),
    FALADOR(17, 34),
    FORT_FORINTHRY(23, 52518),
    FREMENNIK_PROVINCE(29, 18525),
    KARAMJA(30, 18526),
    LUMBRIDGE(18, 35),
    LUNAR_ISLE(10, 9482),
    MENAPHOS(24, 36173),
    OOGLOG(31, 18527),
    PORT_SARIM(19, 36),
    PRIFDDINAS(35, 24967),
    SEERS_VILLAGE(20, 37),
    TAVERLY(21, 38),
    TIRANNWN(32, 18528),
    VARROCK(22, 39),
    WILDERNESS_CRATER(33, 18529),
    YANILLE(26, 40);

    private static final Logger logger = LoggerFactory.getLogger(LodestoneType.class);
    private static final int INTERFACE_ID = 1092;

    private final int componentId;
    private final int varbitId;

    LodestoneType(int componentId, int varbitId) {
        this.componentId = componentId;
        this.varbitId = varbitId;
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
            script.delay(Rand.nextInt(4500, 6500));
        }

        var componentId = this.componentId;
        var component = Interfaces.getComponent(INTERFACE_ID, componentId);
        if (component == null) {
            logger.warn("Component lookup failed for {} (interfaceId={}, componentId={})", this, INTERFACE_ID, componentId);
            return false;
        }

        var interactionResult = component.interact("Teleport");
        if (interactionResult > 0) {
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

        logger.warn("Failed to interact with {} (interaction result: {})", this, interactionResult);
        return false;
    }

    public boolean isAvailable() {
        var result = VarDomain.getVarBitValue(varbitId);
        boolean available;
        switch (this) {
            case LUNAR_ISLE -> available = result >= 100;
            case BANDIT_CAMP -> available = result >= 15;
            default -> available = result == 1;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Availability check for {} returned {} (varbitId={}, value={})", this, available, varbitId, result);
        }
        return available;
    }
}

