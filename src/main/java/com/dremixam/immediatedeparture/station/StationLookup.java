package com.dremixam.immediatedeparture.station;

import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.station.GlobalStation;

/** Resolves a station UUID by searching every currently loaded {@link TrackGraph}. */
public final class StationLookup {
    private StationLookup() {
    }

    public static GlobalStation findById(UUID stationId) {
        for (TrackGraph graph : Create.RAILWAYS.trackNetworks.values()) {
            GlobalStation station = graph.getPoint(EdgePointType.STATION, stationId);
            if (station != null)
                return station;
        }
        return null;
    }
}
