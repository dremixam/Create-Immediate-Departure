package com.dremixam.immediatedeparture.travel.network;

import java.util.UUID;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;

import com.simibubi.create.content.trains.station.GlobalStation;

import com.dremixam.immediatedeparture.station.StationLookup;
import com.dremixam.immediatedeparture.travel.DestinationFinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** C2S: the fast-travel button added to Create's own station screen, asking for the destination list. */
public class RequestDestinationsMessage extends BaseC2SMessage {
    public final UUID originId;

    public RequestDestinationsMessage(UUID originId) {
        this.originId = originId;
    }

    public RequestDestinationsMessage(FriendlyByteBuf buf) {
        this.originId = buf.readUUID();
    }

    @Override
    public MessageType getType() {
        return ImmediateDepartureNetwork.REQUEST_DESTINATIONS;
    }

    private void writeTo(FriendlyByteBuf buf) {
        buf.writeUUID(originId);
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
        if (origin == null)
            return;

        DestinationFinder.openFor(serverPlayer, origin);
    }
}
