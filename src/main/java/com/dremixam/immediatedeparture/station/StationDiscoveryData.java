package com.dremixam.immediatedeparture.station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.mojang.serialization.Codec;

import net.minecraft.core.UUIDUtil;

/**
 * The set of Create stations (by {@code GlobalStation.id}) a single player has discovered.
 * Wrapped in its own type rather than a bare {@code Set<UUID>} so both loaders' attachment APIs
 * (see platforms/fabric, platforms/neoforge) have a single, stable {@link Codec} to persist against.
 */
public final class StationDiscoveryData {
    public static final Codec<StationDiscoveryData> CODEC = UUIDUtil.CODEC.listOf()
        .xmap(ids -> new StationDiscoveryData(new HashSet<>(ids)), data -> new ArrayList<>(data.discoveredStations));

    private final Set<UUID> discoveredStations;

    public StationDiscoveryData() {
        this(new HashSet<>());
    }

    private StationDiscoveryData(Set<UUID> discoveredStations) {
        this.discoveredStations = discoveredStations;
    }

    public boolean hasDiscovered(UUID stationId) {
        return discoveredStations.contains(stationId);
    }

    /** @return true if this station was newly discovered (wasn't already known) */
    public boolean discover(UUID stationId) {
        return discoveredStations.add(stationId);
    }

    public Set<UUID> discoveredStations() {
        return discoveredStations;
    }
}
