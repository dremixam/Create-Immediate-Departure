package com.dremixam.immediatedeparture.platforms.neoforge;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.ImmediateDepartureClient;
import com.dremixam.immediatedeparture.config.ImmediateDepartureConfigScreen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** NeoForge client entry point, equivalent to {@code ImmediateDepartureFabricClient} on Fabric. */
@Mod(value = ImmediateDeparture.MOD_ID, dist = Dist.CLIENT)
public class ImmediateDepartureNeoForgeClient {
    public ImmediateDepartureNeoForgeClient(IEventBus modEventBus, ModContainer modContainer) {
        ImmediateDepartureClient.initClient();
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
            (container, parent) -> ImmediateDepartureConfigScreen.build(parent));
    }
}
