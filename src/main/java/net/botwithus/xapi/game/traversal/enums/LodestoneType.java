package net.botwithus.xapi.game.traversal.enums;

import net.botwithus.xapi.game.traversal.LodestoneNetwork;
import net.botwithus.xapi.script.permissive.base.PermissiveScript;

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
}
