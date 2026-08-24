package com.dremixam.immediatedeparture.travel.network;

import java.util.UUID;

/** One clickable row in {@link com.dremixam.immediatedeparture.travel.DestinationScreen}. */
public record DestinationOption(UUID id, String name) {
}
