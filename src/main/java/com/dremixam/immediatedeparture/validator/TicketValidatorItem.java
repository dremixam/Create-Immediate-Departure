package com.dremixam.immediatedeparture.validator;

import java.util.UUID;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import com.dremixam.immediatedeparture.config.ImmediateDepartureConfig;
import com.dremixam.immediatedeparture.station.StationLookup;
import com.dremixam.immediatedeparture.travel.network.TicketValidatorArmedMessage;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if >=1.21.1 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
//? }

/**
 * Click-to-link placement, mirroring Create's own {@code ClickToLinkBlockItem}.
 * <ul>
 * <li>Right-click a station with an unarmed item: arms it with that station (see
 * {@link TicketValidatorLinking}), no block placed yet.</li>
 * <li>Right-click anywhere else while armed: validates range/dimension against that station, then
 * places the block linked to it.</li>
 * <li>Shift-right-click while armed: clears the pending link.</li>
 * </ul>
 */
public class TicketValidatorItem extends BlockItem {
    public TicketValidatorItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return InteractionResult.FAIL;

        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        UUID pending = TicketValidatorLinking.linkedStationId(stack);

        if (player.isShiftKeyDown() && pending != null) {
            if (!level.isClientSide()) {
                TicketValidatorLinking.clear(stack);
                if (player instanceof ServerPlayer serverPlayer)
                    new TicketValidatorArmedMessage(null, null, null).sendTo(serverPlayer);
                player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.link_cleared")
                    .withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (pending == null) {
            if (!player.isShiftKeyDown()
                && level.getBlockEntity(context.getClickedPos()) instanceof StationBlockEntity stationBlockEntity
                && stationBlockEntity.getStation() != null) {
                armWithStation(level, stationBlockEntity.getStation(), player, stack);
                return InteractionResult.SUCCESS;
            }

            // An unarmed validator must never place as an unlinked block.
            if (!level.isClientSide()) {
                TicketValidatorLinking.clear(stack);
                player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.select_station_first")
                    .withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.FAIL;
        }

        return tryPlace(context, player, stack, pending);
    }

    /** Also called directly from {@link TicketValidatorInteraction}. */
    static void armWithStation(Level level, GlobalStation station, Player player, ItemStack stack) {
        if (level.isClientSide())
            return;

        TicketValidatorLinking.link(stack, station.id);
        if (player instanceof ServerPlayer serverPlayer)
            new TicketValidatorArmedMessage(station.id, station.getBlockEntityPos(), station.getBlockEntityDimension())
                .sendTo(serverPlayer);
        player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.armed", station.name)
            .withStyle(ChatFormatting.AQUA), true);
    }

    private InteractionResult tryPlace(UseOnContext context, Player player, ItemStack stack, UUID stationId) {
        Level level = context.getLevel();
        GlobalStation station = null;

        if (!level.isClientSide()) {
            station = StationLookup.findById(stationId);
            if (station == null) {
                TicketValidatorLinking.clear(stack);
                if (player instanceof ServerPlayer serverPlayer)
                    new TicketValidatorArmedMessage(null, null, null).sendTo(serverPlayer);
                player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.link_failed_missing")
                    .withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            BlockPos clickedPos = context.getClickedPos();
            BlockState clickedState = level.getBlockState(clickedPos);
            BlockPos placedPos = clickedState.canBeReplaced() ? clickedPos : clickedPos.relative(context.getClickedFace());

            boolean sameDimension = level.dimension().equals(station.getBlockEntityDimension());
            double distance = Math.sqrt(placedPos.distSqr(station.getBlockEntityPos()));
            if (!sameDimension || distance > ImmediateDepartureConfig.INSTANCE.ticketValidatorRange) {
                player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.link_failed_range")
                    .withStyle(ChatFormatting.RED), true);
                return InteractionResult.FAIL;
            }

            CompoundTag beTag = new CompoundTag();
            beTag.putUUID(TicketValidatorLinking.LINKED_STATION_KEY, stationId);
            BlockEntity.addEntityType(beTag, ImmediateDepartureBlocks.TICKET_VALIDATOR_BLOCK_ENTITY.get());
            //? if >=1.21.1 {
            CompoundTag existing = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            existing.merge(beTag);
            stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(existing));
            //? } else {
            /*stack.getOrCreateTagElement("BlockEntityTag").merge(beTag);
            *///? }
        }

        InteractionResult result = super.useOn(context);
        if (level.isClientSide() || (result != InteractionResult.SUCCESS && result != InteractionResult.CONSUME))
            return result;

        ItemStack remaining = player.getItemInHand(context.getHand());
        if (!remaining.isEmpty())
            TicketValidatorLinking.clear(remaining);
        if (player instanceof ServerPlayer serverPlayer)
            new TicketValidatorArmedMessage(null, null, null).sendTo(serverPlayer);

        player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.linked", station.name)
            .withStyle(ChatFormatting.GREEN), true);
        return result;
    }
}
