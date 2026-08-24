package com.dremixam.immediatedeparture.validator;

import java.util.UUID;

//? if >=1.21.1 {
import net.minecraft.core.HolderLookup;
//? }
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Holds the id of the station this validator is linked to, if any. See {@link TicketValidatorLinking}. */
public class TicketValidatorBlockEntity extends BlockEntity {
    private UUID linkedStationId;

    public TicketValidatorBlockEntity(BlockPos pos, BlockState state) {
        super(ImmediateDepartureBlocks.TICKET_VALIDATOR_BLOCK_ENTITY.get(), pos, state);
    }

    public UUID linkedStationId() {
        return linkedStationId;
    }

    public void setLinkedStationId(UUID linkedStationId) {
        this.linkedStationId = linkedStationId;
        setChanged();
    }

    //? if >=1.21.1 {
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (linkedStationId != null)
            tag.putUUID(TicketValidatorLinking.LINKED_STATION_KEY, linkedStationId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        linkedStationId = tag.hasUUID(TicketValidatorLinking.LINKED_STATION_KEY) ? tag.getUUID(TicketValidatorLinking.LINKED_STATION_KEY) : null;
    }
    //? } else {
    /*@Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedStationId != null)
            tag.putUUID(TicketValidatorLinking.LINKED_STATION_KEY, linkedStationId);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        linkedStationId = tag.hasUUID(TicketValidatorLinking.LINKED_STATION_KEY) ? tag.getUUID(TicketValidatorLinking.LINKED_STATION_KEY) : null;
    }
    *///? }
}
