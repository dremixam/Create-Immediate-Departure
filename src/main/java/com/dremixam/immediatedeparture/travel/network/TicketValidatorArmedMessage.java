package com.dremixam.immediatedeparture.travel.network;

import java.util.UUID;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;

import com.dremixam.immediatedeparture.validator.TicketValidatorOutline;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * S2C: which station (if any) the client's held Ticket Validator item is currently armed with.
 * Drives the outline highlight. {@code stationId == null} means cleared.
 */
public class TicketValidatorArmedMessage extends BaseS2CMessage {
    public final UUID stationId;
    public final BlockPos stationPos;
    public final ResourceKey<Level> stationDimension;

    public TicketValidatorArmedMessage(UUID stationId, BlockPos stationPos, ResourceKey<Level> stationDimension) {
        this.stationId = stationId;
        this.stationPos = stationPos;
        this.stationDimension = stationDimension;
    }

    public TicketValidatorArmedMessage(FriendlyByteBuf buf) {
        if (buf.readBoolean()) {
            this.stationId = buf.readUUID();
            this.stationPos = buf.readBlockPos();
            this.stationDimension = ResourceKey.create(Registries.DIMENSION, buf.readResourceLocation());
        } else {
            this.stationId = null;
            this.stationPos = null;
            this.stationDimension = null;
        }
    }

    @Override
    public MessageType getType() {
        return ImmediateDepartureNetwork.TICKET_VALIDATOR_ARMED;
    }

    private void writeTo(FriendlyByteBuf buf) {
        buf.writeBoolean(stationId != null);
        if (stationId != null) {
            buf.writeUUID(stationId);
            buf.writeBlockPos(stationPos);
            buf.writeResourceLocation(stationDimension.location());
        }
    }

    //? if >=1.21.1 {
    @Override
    public void write(net.minecraft.network.RegistryFriendlyByteBuf buf) {
        writeTo(buf);
    }
    //? } else {
    /*@Override
    public void write(FriendlyByteBuf buf) {
        writeTo(buf);
    }
    *///? }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        TicketValidatorOutline.setArmedStation(stationId, stationPos, stationDimension);
    }
}
