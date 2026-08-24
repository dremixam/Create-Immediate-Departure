package com.dremixam.immediatedeparture.validator;

import java.util.UUID;

//? if >=1.21.1 {
import com.mojang.serialization.MapCodec;
//? }
import com.simibubi.create.content.trains.station.GlobalStation;

import com.dremixam.immediatedeparture.station.StationLookup;
import com.dremixam.immediatedeparture.travel.DestinationFinder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
//? if >=1.21.1 {
import net.minecraft.world.ItemInteractionResult;
//? } else {
/*import net.minecraft.world.InteractionResult;
*///? }
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A player-facing proxy for a linked station: right-clicking it opens the same destination list as
 * standing at the station itself. Linked via {@link TicketValidatorItem}.
 */
public class TicketValidatorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Bounding box around the model's actual geometry (from Blockbench, in 1/16ths), taller than
    // one block. One box per FACING, matching the blockstate JSON's rotation.
    private static final VoxelShape SHAPE_EAST = Shapes.box(1 / 16.0, 0 / 16.0, 1 / 16.0, 12 / 16.0, 28 / 16.0, 15 / 16.0);
    private static final VoxelShape SHAPE_SOUTH = Shapes.box(1 / 16.0, 0 / 16.0, 1 / 16.0, 15 / 16.0, 28 / 16.0, 12 / 16.0);
    private static final VoxelShape SHAPE_WEST = Shapes.box(4 / 16.0, 0 / 16.0, 1 / 16.0, 15 / 16.0, 28 / 16.0, 15 / 16.0);
    private static final VoxelShape SHAPE_NORTH = Shapes.box(1 / 16.0, 0 / 16.0, 4 / 16.0, 15 / 16.0, 28 / 16.0, 15 / 16.0);

    public TicketValidatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            case NORTH -> SHAPE_NORTH;
            default -> SHAPE_EAST;
        };
    }

    //? if >=1.21.1 {
    @Override
    protected MapCodec<TicketValidatorBlock> codec() {
        return simpleCodec(TicketValidatorBlock::new);
    }
    //? }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TicketValidatorBlockEntity(pos, state);
    }

    //? if >=1.21.1 {
    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
        InteractionHand hand, BlockHitResult hitResult) {
        return open(level, pos, player)
            ? ItemInteractionResult.SUCCESS
            : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
    //? } else {
    /*@Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult hitResult) {
        return open(level, pos, player) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
    *///? }

    private boolean open(Level level, BlockPos pos, Player player) {
        if (level.isClientSide() || !(player instanceof ServerPlayer serverPlayer))
            return false;
        if (!(level.getBlockEntity(pos) instanceof TicketValidatorBlockEntity validator))
            return false;

        UUID stationId = validator.linkedStationId();
        if (stationId == null) {
            player.displayClientMessage(Component.translatable("immediate_departure.ticket_validator.not_linked")
                .withStyle(ChatFormatting.RED), true);
            return true;
        }

        GlobalStation station = StationLookup.findById(stationId);
        if (station == null) {
            player.displayClientMessage(Component.translatable("immediate_departure.station_unavailable")
                .withStyle(ChatFormatting.RED), true);
            return true;
        }

        DestinationFinder.openFor(serverPlayer, station);
        return true;
    }
}
