package com.dremixam.immediatedeparture.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;

//? if fabric {
/*import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
*///? }
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import com.dremixam.immediatedeparture.ImmediateDeparture;
import com.dremixam.immediatedeparture.validator.ImmediateDepartureBlocks;

/** Registers this mod's Ponder scene(s) with Create's Ponder library. */
//? if fabric {
/*@Environment(EnvType.CLIENT)
*///? }
public class ImmediateDeparturePonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return ImmediateDeparture.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        // Tied to the item, not the block, so it shows in the inventory tooltip too.
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(ImmediateDepartureBlocks.TICKET_VALIDATOR_ITEM.get());
        helper.addStoryBoard(itemId, "ticket_validator", TicketValidatorScenes::linking);
    }
}
