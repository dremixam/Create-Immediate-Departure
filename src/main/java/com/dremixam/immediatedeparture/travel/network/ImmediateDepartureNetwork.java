package com.dremixam.immediatedeparture.travel.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;

import com.dremixam.immediatedeparture.ImmediateDeparture;

public final class ImmediateDepartureNetwork {
    private static final SimpleNetworkManager NETWORK = SimpleNetworkManager.create(ImmediateDeparture.MOD_ID);

    public static final MessageType OPEN_DESTINATIONS = NETWORK.registerS2C("open_destinations", OpenDestinationsMessage::new);
    public static final MessageType STATION_DISCOVERED = NETWORK.registerS2C("station_discovered", StationDiscoveredMessage::new);
    public static final MessageType TICKET_VALIDATOR_ARMED = NETWORK.registerS2C("ticket_validator_armed", TicketValidatorArmedMessage::new);
    public static final MessageType TRAVEL_REQUEST = NETWORK.registerC2S("travel_request", TravelRequestMessage::new);
    public static final MessageType REQUEST_DESTINATIONS = NETWORK.registerC2S("request_destinations", RequestDestinationsMessage::new);

    static {
        // Required on 1.21.1, or the dedicated server NPEs when sending an S2C message.
        //? if >=1.21.1 {
        if (dev.architectury.platform.Platform.getEnvironment() != dev.architectury.utils.Env.CLIENT) {
            dev.architectury.networking.NetworkManager.registerS2CPayloadType(OPEN_DESTINATIONS.getId());
            dev.architectury.networking.NetworkManager.registerS2CPayloadType(STATION_DISCOVERED.getId());
            dev.architectury.networking.NetworkManager.registerS2CPayloadType(TICKET_VALIDATOR_ARMED.getId());
        }
        //? }
    }

    private ImmediateDepartureNetwork() {
    }

    /** No-op body; calling this forces the static fields above to initialize. */
    public static void register() {
    }
}
