package com.dremixam.immediatedeparture.station;

import java.util.Set;
import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;

/**
 * Bridges the shared discovery logic to each loader's own persistent player-data API. See
 * platforms/fabric and platforms/neoforge for the two implementations.
 */
public interface PlayerStationTracker {
    boolean hasDiscovered(ServerPlayer player, UUID stationId);

    /** @return true if this station was newly discovered (wasn't already known) */
    boolean markDiscovered(ServerPlayer player, UUID stationId);

    Set<UUID> discovered(ServerPlayer player);
}
