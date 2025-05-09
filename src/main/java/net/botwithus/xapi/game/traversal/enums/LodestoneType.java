package net.botwithus.xapi.game.traversal.enums;

import net.botwithus.rs3.entities.LocalPlayer;
import net.botwithus.rs3.minimenu.MiniMenu;
import net.botwithus.rs3.vars.VarDomain;
import net.botwithus.rs3.minimenu.Action;
import net.botwithus.util.Rand;
import net.botwithus.xapi.game.traversal.LodestoneNetwork;
import net.botwithus.xapi.script.base.DelayableScript;

public enum LodestoneType {
    AL_KHARID(71565323, 28),
    ANACHRONIA(71565337, 44270),
    ARDOUGNE(71565324, 29),
    ASHDALE(71565346, 35), // TODO Need Varbit to know if they're available or not
    BANDIT_CAMP(71565321, 9482),
    BURTHORPE(71565325, 30),
    CANIFIS(71565339, 18523),
    CATHERBY(71565326, 31),
    CITY_OF_UM(71565348, 35), // TODO Need Varbit to know if they're available or not
    DRAYNOR_VILLAGE(71565327, 32),
    EDGEVILLE(71565328, 33),
    EAGLES_PEAK(71565340, 18524),
    FALADOR(71565329, 34),
    FORT_FORINTHRY(71565335, 35), // TODO Need Varbit to know if they're available or not
    FREMENNIK_PROVINCE(71565341, 18525),
    KARAMJA(71565342, 18526),
    LUMBRIDGE(71565330, 35),
    LUNAR_ISLE(71565322, 9482),
    MENAPHOS(71565336, 35), // TODO Need Varbit to know if they're available or not
    OOGLOG(71565343, 18527),
    PORT_SARIM(71565331, 36),
    PRIFDDINAS(71565347, 24967),
    SEERS_VILLAGE(71565332, 37),
    TAVERLY(71565333, 38),
    TIRANNWN(71565344, 18528),
    VARROCK(71565334, 39),
    WILDERNESS_CRATER(71565345, 18529),
    YANILLE(71565338, 40);

    private final int interactId;
    private final int varbitId;

    //TODO: Add logging

    LodestoneType(int interactId, int varbitId) {
        this.interactId = interactId;
        this.varbitId = varbitId;
    }

    //TODO: Update to no longer use MiniMenu.doAction
    public boolean teleport(DelayableScript script) {
        var player = LocalPlayer.self();
        if (player == null) {
            return false;
        }
//        var coordinate = player.getCoordinate();
        boolean validate = !LodestoneNetwork.isOpen();
//        log.atInfo().log("[Lodestone] LodestoneNetworkIsNotOpen: " + validate);
        if (validate) {
            LodestoneNetwork.open();
            script.delay(Rand.nextInt(600, 900));
        }
        if (MiniMenu.doAction(Action.COMPONENT, 1, -1, interactId)) {
            int wax = VarDomain.getVarBitValue(28623);
            int quick = VarDomain.getVarBitValue(28622);
            if (quick == 1 && wax > 0) {
                script.delay(Rand.nextInt(4500, 6500));
            } else {
                script.delay(Rand.nextInt(12000, 14000));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isAvailable() {
        var result = VarDomain.getVarBitValue(varbitId);
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
}
