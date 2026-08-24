package com.dremixam.immediatedeparture.travel;

/** Why {@link FastTravelValidator} refused a trip, surfaced to the player by whatever UI calls it. */
public enum TravelDenialReason {
    DESTINATION_NOT_DISCOVERED,
    NO_RAIL_CONNECTION,
    NO_ACTIVE_SCHEDULE
}
