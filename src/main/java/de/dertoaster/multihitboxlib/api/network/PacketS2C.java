package de.dertoaster.multihitboxlib.api.network;

import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * abstract class used for creating packets that are sent from the server to the client. It
 * provides a structure for handling the data buffer and the packet's channel name,
 * as well as methods for sending and receiving the packet.
 * **/
public abstract class PacketS2C {
    /** The player that the packet is being sent to. **/
    private final ServerPlayer player;
    /** The buffer that holds the packet data. **/
    private final FriendlyByteBuf buf;

    /**
     * Initializes the packet with the given player and buffer and registers
     * the packet to be sent from the server to the client.
     * <br>
     *
     * If you want to add new fields to the packet, you can do it here
     * and then read/write them in the receive() and send() method.
     * **/
    public PacketS2C(ServerPlayer player, FriendlyByteBuf buf) {
        this.player = player;
        this.buf = buf;
        MHLibPackets.registerServerToClientPacket(this);
    }

    /** Abstract method to handle the buffer on the server side. **/
    public abstract void send();

    /** Abstract method to handle the packet reception on the client side. **/
    public abstract void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender);

    /** Abstract method to get the channel name for the packet. **/
    public ServerPlayer getPlayer() {
        return player;
    }

    /** Abstract method to get the channel name for the packet. **/
    public abstract ResourceLocation getChannelName();

    /** Getter for the packet's buffer. **/
    public FriendlyByteBuf getBuf() {
        return buf;
    }
}
