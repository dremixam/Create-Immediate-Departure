package com.dremixam.immediatedeparture.platforms.neoforge;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

/**
 * NeoForge entry point. Only keeps what NeoForge natively requires; everything else delegates to
 * {@link ImmediateDeparture#init}, shared by both loaders.
 */
@Mod(ImmediateDeparture.MOD_ID)
public class ImmediateDepartureNeoForge {
    public ImmediateDepartureNeoForge(IEventBus modEventBus) {
        NeoForgePlayerStationTracker.register(modEventBus);
        ImmediateDeparture.init(FMLPaths.CONFIGDIR.get(), new NeoForgePlayerStationTracker());
    }
}
