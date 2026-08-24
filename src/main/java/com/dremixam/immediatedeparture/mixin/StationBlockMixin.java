package com.dremixam.immediatedeparture.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlock;
import com.simibubi.create.content.trains.station.StationBlockEntity;

import com.dremixam.immediatedeparture.travel.DestinationFinder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.1 {
import net.minecraft.world.ItemInteractionResult;
//? } else {
/*import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
*///? }
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Opens the destination picker on shift-right-click of a station. Normal clicks are untouched and
 * still open Create's own station screen.
 */
@Mixin(StationBlock.class)
public abstract class StationBlockMixin {

    //? if >=1.21.1 {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void immediateDeparture$openDestinationList(ItemStack stack, BlockState state, Level level, BlockPos pos,
        net.minecraft.world.entity.player.Player player, InteractionHand hand, BlockHitResult hitResult,
        CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (open(level, pos, player))
            cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
    //? } else {
    /*@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void immediateDeparture$openDestinationList(BlockState state, Level level, BlockPos pos, Player player,
        InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (open(level, pos, player))
            cir.setReturnValue(InteractionResult.SUCCESS);
    }
    *///? }

    private static boolean open(Level level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        if (level.isClientSide() || !player.isShiftKeyDown() || !(player instanceof ServerPlayer serverPlayer))
            return false;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof StationBlockEntity stationBlockEntity))
            return false;

        GlobalStation origin = stationBlockEntity.getStation();
        if (origin == null)
            return false;

        DestinationFinder.openFor(serverPlayer, origin);
        return true;
    }
}
