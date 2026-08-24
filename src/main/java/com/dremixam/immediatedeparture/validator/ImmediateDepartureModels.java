package com.dremixam.immediatedeparture.validator;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

import com.dremixam.immediatedeparture.ImmediateDeparture;

import net.minecraft.resources.ResourceLocation;

/**
 * Forces {@code TICKET_VALIDATOR_SCREEN} to be constructed before Flywheel's model-bake pass, via
 * {@link #init()} called eagerly from {@code initClient()}.
 */
public final class ImmediateDepartureModels {
    //? if >=1.21.1 {
    public static final PartialModel TICKET_VALIDATOR_SCREEN = PartialModel.of(
        ResourceLocation.fromNamespaceAndPath(ImmediateDeparture.MOD_ID, "block/ticket_validator_screen"));
    //? } else {
    /*public static final PartialModel TICKET_VALIDATOR_SCREEN = PartialModel.of(
        ResourceLocation.tryBuild(ImmediateDeparture.MOD_ID, "block/ticket_validator_screen"));
    *///? }

    private ImmediateDepartureModels() {
    }

    public static void init() {
        // Intentionally empty; forces the static fields above to run.
    }
}
