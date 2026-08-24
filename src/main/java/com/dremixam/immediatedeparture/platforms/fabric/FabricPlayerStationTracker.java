package com.dremixam.immediatedeparture.platforms.fabric;

import java.util.Set;
import java.util.UUID;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.station.PlayerStationTracker;
import com.dremixam.immediatedeparture.station.StationDiscoveryData;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class FabricPlayerStationTracker implements PlayerStationTracker {

    private static final AttachmentType<StationDiscoveryData> DISCOVERED_STATIONS = AttachmentRegistry.createPersistent(
        //? if >=1.21.1 {
        /*ResourceLocation.fromNamespaceAndPath(ImmediateDeparture.MOD_ID, "discovered_stations"),*/
        //? } else {
        ResourceLocation.tryBuild(ImmediateDeparture.MOD_ID, "discovered_stations"),
        //? }
        StationDiscoveryData.CODEC
    );

    @Override
    public boolean hasDiscovered(ServerPlayer player, UUID stationId) {
        return data(player).hasDiscovered(stationId);
    }

    @Override
    public boolean markDiscovered(ServerPlayer player, UUID stationId) {
        return data(player).discover(stationId);
    }

    @Override
    public Set<UUID> discovered(ServerPlayer player) {
        return data(player).discoveredStations();
    }

    private StationDiscoveryData data(ServerPlayer player) {
        return player.getAttachedOrCreate(DISCOVERED_STATIONS, StationDiscoveryData::new);
    }
}
