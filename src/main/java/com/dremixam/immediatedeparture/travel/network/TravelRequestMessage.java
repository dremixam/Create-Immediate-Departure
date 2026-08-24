package com.dremixam.immediatedeparture.travel.network;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;

import com.simibubi.create.content.trains.station.GlobalStation;

import com.dremixam.immediatedeparture.station.StationLookup;
import com.dremixam.immediatedeparture.travel.FastTravelExecutor;
import com.dremixam.immediatedeparture.travel.FastTravelValidator;
import com.dremixam.immediatedeparture.travel.TravelDenialReason;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * C2S: a destination clicked in {@link com.dremixam.immediatedeparture.travel.DestinationScreen}.
 * Re-validates server-side with {@link FastTravelValidator}; the client's list is never trusted on
 * its own.
 */
public class TravelRequestMessage extends BaseC2SMessage {
    public final UUID originId;
    public final UUID destinationId;

    public TravelRequestMessage(UUID originId, UUID destinationId) {
        this.originId = originId;
        this.destinationId = destinationId;
    }

    public TravelRequestMessage(FriendlyByteBuf buf) {
        this.originId = buf.readUUID();
        this.destinationId = buf.readUUID();
    }

    @Override
    public MessageType getType() {
        return ImmediateDepartureNetwork.TRAVEL_REQUEST;
    }

    private void writeTo(FriendlyByteBuf buf) {
        buf.writeUUID(originId);
        buf.writeUUID(destinationId);
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
        Player player = context.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        GlobalStation origin = StationLookup.findById(originId);
        GlobalStation destination = StationLookup.findById(destinationId);
        if (origin == null || destination == null) {
            serverPlayer.sendSystemMessage(
                Component.translatable("immediate_departure.station_unavailable").withStyle(ChatFormatting.RED));
            return;
        }

        Optional<TravelDenialReason> denial =
            FastTravelValidator.validate(serverPlayer, serverPlayer.serverLevel(), origin, destination);
        if (denial.isPresent()) {
            String key = "immediate_departure.travel_denied." + denial.get().name().toLowerCase(Locale.ROOT);
            serverPlayer.sendSystemMessage(Component.translatable(key).withStyle(ChatFormatting.RED));
            return;
        }

        FastTravelExecutor.travel(serverPlayer, destination);
    }
}
