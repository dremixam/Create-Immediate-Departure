package com.dremixam.immediatedeparture.validator;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//? }

/** Stores the station a Ticket Validator item is bound to, directly on the item stack. */
public final class TicketValidatorLinking {
    public static final String LINKED_STATION_KEY = "LinkedStation";

    private TicketValidatorLinking() {
    }

    //? if >=1.21.1 {
    public static void link(ItemStack stack, UUID stationId) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putUUID(LINKED_STATION_KEY, stationId);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static UUID linkedStationId(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.hasUUID(LINKED_STATION_KEY) ? tag.getUUID(LINKED_STATION_KEY) : null;
    }

    /** Clears both the pending link and any seeded block-entity data; the two must be cleared together. */
    public static void clear(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.remove(LINKED_STATION_KEY);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);
    }
    //? } else {
    /*public static void link(ItemStack stack, UUID stationId) {
        stack.getOrCreateTag().putUUID(LINKED_STATION_KEY, stationId);
    }

    public static UUID linkedStationId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.hasUUID(LINKED_STATION_KEY) ? tag.getUUID(LINKED_STATION_KEY) : null;
    }

    public static void clear(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null)
            tag.remove(LINKED_STATION_KEY);
        stack.removeTagKey("BlockEntityTag");
    }
    *///? }
}
