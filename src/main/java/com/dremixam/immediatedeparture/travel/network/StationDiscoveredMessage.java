package com.dremixam.immediatedeparture.travel.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;

import com.dremixam.immediatedeparture.ImmediateDepartureClient;

import net.minecraft.network.FriendlyByteBuf;

/** S2C: a station this player just discovered, shown as a toast. */
public class StationDiscoveredMessage extends BaseS2CMessage {
    public final String stationName;

    public StationDiscoveredMessage(String stationName) {
        this.stationName = stationName;
    }

    public StationDiscoveredMessage(FriendlyByteBuf buf) {
        this.stationName = buf.readUtf();
    }

    @Override
    public MessageType getType() {
        return ImmediateDepartureNetwork.STATION_DISCOVERED;
    }

    private void writeTo(FriendlyByteBuf buf) {
        buf.writeUtf(stationName);
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
        ImmediateDepartureClient.showDiscoveryToast(stationName);
    }
}
