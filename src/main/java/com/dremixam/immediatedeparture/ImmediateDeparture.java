package com.dremixam.immediatedeparture;

import java.nio.file.Path;

import dev.architectury.event.events.common.LifecycleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dremixam.immediatedeparture.config.ImmediateDepartureConfig;
import com.dremixam.immediatedeparture.station.PlayerStationTracker;
import com.dremixam.immediatedeparture.travel.network.ImmediateDepartureNetwork;
import com.dremixam.immediatedeparture.validator.ImmediateDepartureBlocks;
import com.dremixam.immediatedeparture.validator.TicketValidatorInteraction;

/**
 * Entry point shared by both loaders: each platform calls {@link #init(Path, PlayerStationTracker)}
 * from its own native entry point.
 */
public final class ImmediateDeparture {
    public static final String MOD_ID = "create_immediate_departure";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static PlayerStationTracker stationTracker;

    private ImmediateDeparture() {
    }

    public static void init(Path configDir, PlayerStationTracker tracker) {
        stationTracker = tracker;
        ImmediateDepartureConfig.load(configDir);
        ImmediateDepartureNetwork.register();
        ImmediateDepartureBlocks.register();
        TicketValidatorInteraction.register();

        LOGGER.info("Create: Immediate Departure initialized");

        LifecycleEvent.SERVER_STARTED.register(server ->
            LOGGER.info("Create: Immediate Departure: server started")
        );
    }
}
