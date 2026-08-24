package com.dremixam.immediatedeparture.travel;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.station.GlobalStation;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/** Actually moves the player once {@link FastTravelValidator} has already approved the trip. */
public final class FastTravelExecutor {
    private FastTravelExecutor() {
    }

    public static void travel(ServerPlayer player, GlobalStation destination) {
        ServerLevel destinationLevel = player.server.getLevel(destination.getBlockEntityDimension());
        if (destinationLevel == null)
            return;

        BlockPos pos = destination.getBlockEntityPos();
        Vec3 spot = groundSpotBesideStation(pos, destination);

        player.closeContainer();
        player.stopRiding();

        AllSoundEvents.WHISTLE_TRAIN.playOnServer(player.serverLevel(), player.blockPosition());

        if (destinationLevel == player.serverLevel()) {
            // teleportTo(ServerLevel, ...) can fall back to the level's spawn point here on 1.20.1.
            player.teleportTo(spot.x, spot.y, spot.z);
        } else {
            player.teleportTo(destinationLevel, spot.x, spot.y, spot.z, player.getYRot(), player.getXRot());
        }

        AllSoundEvents.WHISTLE_TRAIN.playOnServer(destinationLevel, pos);
    }

    /** Ground level beside the station block, offset away from its track so the player doesn't land on the rails. */
    private static Vec3 groundSpotBesideStation(BlockPos pos, GlobalStation destination) {
        Vec3 stationCenter = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        Vec3 trackPos = destination.edgeLocation.getFirst().getLocation();
        Vec3 awayFromTrack = stationCenter.subtract(trackPos).multiply(1, 0, 1);
        if (awayFromTrack.lengthSqr() < 1.0E-4)
            awayFromTrack = new Vec3(1, 0, 0);
        awayFromTrack = awayFromTrack.normalize();
        return stationCenter.add(awayFromTrack.scale(1.0));
    }
}
