package com.dremixam.immediatedeparture.validator;

import com.mojang.blaze3d.vertex.PoseStack;

import net.createmod.catnip.render.CachedBuffers;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

/**
 * Renders the validator's screen separately from the baked block model, at full brightness with
 * alpha blending, using the partial model {@link ImmediateDepartureModels#TICKET_VALIDATOR_SCREEN}.
 * Rotated to match {@code FACING}.
 */
public class TicketValidatorRenderer implements BlockEntityRenderer<TicketValidatorBlockEntity> {
    public TicketValidatorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TicketValidatorBlockEntity blockEntity, float partialTick, PoseStack poseStack,
        MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction facing = blockEntity.getBlockState().getValue(TicketValidatorBlock.FACING);
        CachedBuffers.partial(ImmediateDepartureModels.TICKET_VALIDATOR_SCREEN, blockEntity.getBlockState())
            .rotateCentered((float) -Math.toRadians(yRotationDegrees(facing)), Direction.UP)
            .light(LightTexture.FULL_BRIGHT)
            .overlay(packedOverlay)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.translucent()));
    }

    // Matches the blockstate JSON's "y" values for each facing.
    private static float yRotationDegrees(Direction facing) {
        return switch (facing) {
            case NORTH -> 270;
            case SOUTH -> 90;
            case WEST -> 180;
            default -> 0;
        };
    }
}
