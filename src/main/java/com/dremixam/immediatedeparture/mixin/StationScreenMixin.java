package com.dremixam.immediatedeparture.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;

import com.dremixam.immediatedeparture.travel.ModIcon;
import com.dremixam.immediatedeparture.travel.network.RequestDestinationsMessage;

import net.minecraft.network.chat.Component;

/**
 * Adds a fast-travel button to Create's own station screen, next to its existing confirm button.
 * Extends {@code AbstractStationScreen} so this file compiles with normal access to
 * guiLeft/guiTop/background/station; Mixin discards this fake hierarchy at merge time.
 */
@Mixin(StationScreen.class)
public abstract class StationScreenMixin extends AbstractStationScreen {

    protected StationScreenMixin(StationBlockEntity be, GlobalStation station) {
        super(be, station);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void immediateDeparture$addFastTravelButton(CallbackInfo ci) {
        // Placed just outside the panel's left edge, level with the confirm button.
        IconButton fastTravelButton = new IconButton(
            guiLeft - 22, guiTop + background.getHeight() - 24, ModIcon.TICKET);
        fastTravelButton.setToolTip(Component.translatable("immediate_departure.fast_travel_button"));
        fastTravelButton.withCallback(() -> new RequestDestinationsMessage(station.id).sendToServer());
        addRenderableWidget(fastTravelButton);
    }
}
