package com.dremixam.immediatedeparture.travel.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;

import com.dremixam.immediatedeparture.ImmediateDepartureClient;

import net.minecraft.network.FriendlyByteBuf;

/**
 * S2C: the reachable destinations for a station the player just interacted with, computed
 * server-side and sent to open {@link com.dremixam.immediatedeparture.travel.DestinationScreen}.
 */
public class OpenDestinationsMessage extends BaseS2CMessage {
    public final UUID originId;
    public final String originName;
    public final List<DestinationOption> destinations;

    public OpenDestinationsMessage(UUID originId, String originName, List<DestinationOption> destinations) {
        this.originId = originId;
        this.originName = originName;
        this.destinations = destinations;
    }

    public OpenDestinationsMessage(FriendlyByteBuf buf) {
        this.originId = buf.readUUID();
        this.originName = buf.readUtf();
        int count = buf.readVarInt();
        List<DestinationOption> read = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
            read.add(new DestinationOption(buf.readUUID(), buf.readUtf()));
        this.destinations = read;
    }

    @Override
    public MessageType getType() {
        return ImmediateDepartureNetwork.OPEN_DESTINATIONS;
    }

    private void writeTo(FriendlyByteBuf buf) {
        buf.writeUUID(originId);
        buf.writeUtf(originName);
        buf.writeVarInt(destinations.size());
        for (DestinationOption destination : destinations) {
            buf.writeUUID(destination.id());
            buf.writeUtf(destination.name());
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
        ImmediateDepartureClient.openDestinationScreen(originId, originName, destinations);
    }
}
