package com.dremixam.immediatedeparture.travel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.simibubi.create.content.trains.station.GlobalStation;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.station.StationLookup;
import com.dremixam.immediatedeparture.travel.network.DestinationOption;
import com.dremixam.immediatedeparture.travel.network.OpenDestinationsMessage;

import net.minecraft.server.level.ServerPlayer;

/**
 * Server-side half of the destination picker: computes every station this player has discovered
 * and can currently reach from {@code origin}, then sends it to render as
 * {@link com.dremixam.immediatedeparture.travel.DestinationScreen}.
 */
public final class DestinationFinder {
    private DestinationFinder() {
    }

    public static void openFor(ServerPlayer player, GlobalStation origin) {
        List<DestinationOption> reachable = new ArrayList<>();
        for (UUID stationId : ImmediateDeparture.stationTracker.discovered(player)) {
            GlobalStation candidate = StationLookup.findById(stationId);
            if (candidate == null || candidate == origin)
                continue;
            if (FastTravelValidator.validate(player, player.serverLevel(), origin, candidate).isEmpty())
                reachable.add(new DestinationOption(candidate.id, candidate.name));
        }

        new OpenDestinationsMessage(origin.id, origin.name, reachable).sendTo(player);
    }
}
