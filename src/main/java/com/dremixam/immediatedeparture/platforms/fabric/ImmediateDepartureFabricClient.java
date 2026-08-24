package com.dremixam.immediatedeparture.platforms.fabric;

import com.dremixam.immediatedeparture.ImmediateDepartureClient;
import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric client entry point (the "client" entrypoint in fabric.mod.json, distinct from "main").
 * Only loaded in a client environment, never on a dedicated server.
 */
public class ImmediateDepartureFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ImmediateDepartureClient.initClient();
    }
}
