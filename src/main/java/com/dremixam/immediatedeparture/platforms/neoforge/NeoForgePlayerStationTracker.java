package com.dremixam.immediatedeparture.platforms.neoforge;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.station.PlayerStationTracker;
import com.dremixam.immediatedeparture.station.StationDiscoveryData;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class NeoForgePlayerStationTracker implements PlayerStationTracker {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ImmediateDeparture.MOD_ID);

    private static final Supplier<AttachmentType<StationDiscoveryData>> DISCOVERED_STATIONS = ATTACHMENT_TYPES.register(
        "discovered_stations",
        () -> AttachmentType.builder(StationDiscoveryData::new)
            .serialize(StationDiscoveryData.CODEC)
            .build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    @Override
    public boolean hasDiscovered(ServerPlayer player, UUID stationId) {
        return player.getData(DISCOVERED_STATIONS).hasDiscovered(stationId);
    }

    @Override
    public boolean markDiscovered(ServerPlayer player, UUID stationId) {
        return player.getData(DISCOVERED_STATIONS).discover(stationId);
    }

    @Override
    public Set<UUID> discovered(ServerPlayer player) {
        return player.getData(DISCOVERED_STATIONS).discoveredStations();
    }
}
