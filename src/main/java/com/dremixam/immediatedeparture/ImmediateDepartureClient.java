package com.dremixam.immediatedeparture;

import java.util.List;
import java.util.UUID;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientTickEvent;
//? if fabric {
/*import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
*///? }

import com.dremixam.immediatedeparture.ponder.ImmediateDeparturePonderPlugin;
import com.dremixam.immediatedeparture.travel.DestinationScreen;
import com.dremixam.immediatedeparture.travel.network.DestinationOption;
import com.dremixam.immediatedeparture.validator.ImmediateDepartureBlocks;
import com.dremixam.immediatedeparture.validator.ImmediateDepartureModels;
import com.dremixam.immediatedeparture.validator.TicketValidatorOutline;
import com.dremixam.immediatedeparture.validator.TicketValidatorRenderer;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

import net.createmod.ponder.foundation.PonderIndex;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/** Client-side logic shared by both loaders. */
//? if fabric {
/*@Environment(EnvType.CLIENT)
*///? }
public final class ImmediateDepartureClient {
    private ImmediateDepartureClient() {
    }

    public static void initClient() {
        ImmediateDeparture.LOGGER.info("Create: Immediate Departure: client-side init");

        ImmediateDepartureModels.init();

        // Must be registered here, not directly in the constructor, or it NPEs on NeoForge.
        ClientLifecycleEvent.CLIENT_SETUP.register(client ->
            BlockEntityRendererRegistry.register(ImmediateDepartureBlocks.TICKET_VALIDATOR_BLOCK_ENTITY.get(), TicketValidatorRenderer::new)
        );

        ClientLifecycleEvent.CLIENT_STARTED.register(client ->
            ImmediateDeparture.LOGGER.info("Create: Immediate Departure: client started")
        );

        ClientTickEvent.CLIENT_POST.register(client -> TicketValidatorOutline.tick());

        PonderIndex.addPlugin(new ImmediateDeparturePonderPlugin());
    }

    public static void openDestinationScreen(UUID originId, String originName, List<DestinationOption> destinations) {
        Minecraft.getInstance().setScreen(new DestinationScreen(originId, originName, destinations));
    }

    // SystemToast's id class was renamed SystemToastIds -> SystemToastId from 1.20.2 on.
    //? if >=1.21.1 {
    private static final SystemToast.SystemToastId STATION_DISCOVERED_TOAST = new SystemToast.SystemToastId();

    public static void showDiscoveryToast(String stationName) {
        SystemToast.add(Minecraft.getInstance().getToasts(), STATION_DISCOVERED_TOAST,
            Component.literal(stationName), Component.translatable("immediate_departure.toast.station_discovered"));
    }
    //? } else {
    /*private static final SystemToast.SystemToastIds STATION_DISCOVERED_TOAST = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION;

    public static void showDiscoveryToast(String stationName) {
        SystemToast.add(Minecraft.getInstance().getToasts(), STATION_DISCOVERED_TOAST,
            Component.literal(stationName), Component.translatable("immediate_departure.toast.station_discovered"));
    }
    *///? }
}
