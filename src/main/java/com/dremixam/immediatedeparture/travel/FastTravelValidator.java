package com.dremixam.immediatedeparture.travel;

import java.util.Optional;

import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.ScheduleRuntime;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.config.ImmediateDepartureConfig;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * Decides whether a player may fast-travel from {@code origin} to {@code destination}. Pure
 * logic, no side effects; callers act on the result.
 */
public final class FastTravelValidator {
    private FastTravelValidator() {
    }

    public static Optional<TravelDenialReason> validate(ServerPlayer player, Level level, GlobalStation origin,
        GlobalStation destination) {
        if (!ImmediateDeparture.stationTracker.hasDiscovered(player, destination.id))
            return Optional.of(TravelDenialReason.DESTINATION_NOT_DISCOVERED);

        if (!connectedByRail(level, origin, destination))
            return Optional.of(TravelDenialReason.NO_RAIL_CONNECTION);

        if (ImmediateDepartureConfig.INSTANCE.requireActiveSchedule && !servedByActiveSchedule(origin, destination))
            return Optional.of(TravelDenialReason.NO_ACTIVE_SCHEDULE);

        return Optional.empty();
    }

    /** Two stations are rail-connected when they sit on the same {@code TrackGraph}. */
    private static boolean connectedByRail(Level level, GlobalStation origin, GlobalStation destination) {
        TrackGraph originGraph = Create.RAILWAYS.sided(level).getGraph(level, origin.edgeLocation.getFirst());
        TrackGraph destinationGraph = Create.RAILWAYS.sided(level).getGraph(level, destination.edgeLocation.getFirst());
        return originGraph != null && destinationGraph != null && originGraph.id.equals(destinationGraph.id);
    }

    /**
     * Best-effort: matches each station's name against the destination filters of every train's
     * currently active schedule. An approximation of "this schedule serves both stations", not a
     * simulation of the actual route.
     */
    private static boolean servedByActiveSchedule(GlobalStation origin, GlobalStation destination) {
        for (Train train : Create.RAILWAYS.trains.values()) {
            ScheduleRuntime runtime = train.runtime;
            Schedule schedule = runtime.schedule;
            if (schedule == null || runtime.paused || runtime.completed)
                continue;

            boolean servesOrigin = false;
            boolean servesDestination = false;
            for (ScheduleEntry entry : schedule.entries) {
                if (!(entry.instruction instanceof DestinationInstruction destinationInstruction))
                    continue;
                String regex = destinationInstruction.getFilterForRegex();
                servesOrigin |= origin.name.matches(regex);
                servesDestination |= destination.name.matches(regex);
            }
            if (servesOrigin && servesDestination)
                return true;
        }
        return false;
    }
}
