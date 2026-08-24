package com.dremixam.immediatedeparture.mixin;

import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.config.ImmediateDepartureConfig;
import com.dremixam.immediatedeparture.travel.network.StationDiscoveredMessage;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Discovery Condition: proximity to a station is enough to discover it. Each station scans for
 * nearby players once a second.
 */
@Mixin(StationBlockEntity.class)
public abstract class StationBlockEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void immediateDeparture$scanForDiscovery(CallbackInfo ci) {
        StationBlockEntity self = (StationBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide() || level.getGameTime() % 20 != 0)
            return;

        GlobalStation station = self.getStation();
        if (station == null)
            return;

        AABB range = new AABB(self.getBlockPos()).inflate(ImmediateDepartureConfig.INSTANCE.discoveryRadius);
        List<ServerPlayer> nearbyPlayers = level.getEntitiesOfClass(ServerPlayer.class, range);

        UUID stationId = station.id;
        for (ServerPlayer player : nearbyPlayers) {
            if (!ImmediateDeparture.stationTracker.markDiscovered(player, stationId))
                continue;

            new StationDiscoveredMessage(station.name).sendTo(player);
            player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.6f, 1.4f);
        }
    }
}
