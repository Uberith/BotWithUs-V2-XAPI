package net.botwithus.xapi.game.traversal;

import net.botwithus.rs3.interfaces.Interfaces;
import net.botwithus.xapi.query.ComponentQuery;

public class LodestoneNetwork {
    public static boolean isOpen() {
        return Interfaces.isOpen(1092);
    }

    /**
     * Opens the Lodestone Network interface.
     *
     * @return {@code true} if the interface was opened, {@code false} otherwise.
     */
    public static boolean open() {
        var result = ComponentQuery.newQuery(1465).option("Lodestone network").results().first();
        return result != null && result.interact("Lodestone network") > 0;
    }

    /**
     * Teleports the player to their previous destination.
     *
     * @return true if the player was successfully teleported, false otherwise
     */
    public static boolean teleportToPreviousDestination() {
        var result = ComponentQuery.newQuery(1465).option("Previous Destination").results().first();
        return result != null && result.interact("Previous Destination") > 0;
    }
}
