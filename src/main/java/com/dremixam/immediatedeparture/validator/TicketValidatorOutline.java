package com.dremixam.immediatedeparture.validator;

import java.util.UUID;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Draws Create's outline highlight around the station a Ticket Validator item is currently armed
 * with. The armed station is pushed from the server via {@link #setArmedStation}; {@link #tick}
 * hides the highlight again once the player switches away from that item.
 */
public final class TicketValidatorOutline {
    private static final int COLOR = 0xFFCB74;

    private static UUID armedStationId;
    private static BlockPos armedStationPos;
    private static ResourceKey<Level> armedStationDimension;

    private TicketValidatorOutline() {
    }

    public static void setArmedStation(UUID stationId, BlockPos stationPos, ResourceKey<Level> stationDimension) {
        armedStationId = stationId;
        armedStationPos = stationPos;
        armedStationDimension = stationDimension;
    }

    public static void tick() {
        if (armedStationId == null)
            return;

        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(ImmediateDepartureBlocks.TICKET_VALIDATOR_ITEM.get())
            || !armedStationId.equals(TicketValidatorLinking.linkedStationId(heldItem)))
            return;

        Level level = Minecraft.getInstance().level;
        if (level == null || !level.dimension().equals(armedStationDimension))
            return;

        BlockState state = level.getBlockState(armedStationPos);
        VoxelShape shape = state.getShape(level, armedStationPos);
        AABB bounds = shape.isEmpty() ? new AABB(armedStationPos) : shape.bounds().move(armedStationPos);

        Outliner.getInstance().showAABB(armedStationId, bounds)
            .colored(COLOR)
            .lineWidth(1 / 16f);
    }
}
