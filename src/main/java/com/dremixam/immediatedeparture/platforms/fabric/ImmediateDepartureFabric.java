package com.dremixam.immediatedeparture.platforms.fabric;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Fabric entry point. Only keeps what Fabric natively requires; everything else delegates to
 * {@link ImmediateDeparture#init}, shared by both loaders.
 */
public class ImmediateDepartureFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ImmediateDeparture.init(FabricLoader.getInstance().getConfigDir(), new FabricPlayerStationTracker());
    }
}
