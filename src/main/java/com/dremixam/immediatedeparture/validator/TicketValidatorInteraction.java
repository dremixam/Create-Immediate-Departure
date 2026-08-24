package com.dremixam.immediatedeparture.validator;

import com.simibubi.create.content.trains.station.StationBlockEntity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Registers the click-to-link handler on {@code InteractionEvent.RIGHT_CLICK_BLOCK}, which fires
 * before vanilla's block/item interaction chain. Only handles selecting a station with an unarmed
 * item; placing and shift-clearing are left to {@link TicketValidatorItem#useOn}.
 */
public final class TicketValidatorInteraction {
    private TicketValidatorInteraction() {
    }

    public static void register() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(TicketValidatorInteraction::onRightClickBlock);
    }

    private static EventResult onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(ImmediateDepartureBlocks.TICKET_VALIDATOR_ITEM.get()))
            return EventResult.pass();
        if (player.isShiftKeyDown() || TicketValidatorLinking.linkedStationId(stack) != null)
            return EventResult.pass();

        Level level = player.level();
        if (!(level.getBlockEntity(pos) instanceof StationBlockEntity stationBlockEntity) || stationBlockEntity.getStation() == null)
            return EventResult.pass();

        TicketValidatorItem.armWithStation(level, stationBlockEntity.getStation(), player, stack);
        // Must return SUCCESS, not FAIL, or Fabric won't forward the interaction to the server.
        return EventResult.interruptTrue();
    }
}
