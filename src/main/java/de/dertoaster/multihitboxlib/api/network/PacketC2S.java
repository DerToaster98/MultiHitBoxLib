package de.dertoaster.multihitboxlib.api.network;

import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

/**
 * abstract class used for creating packets that are sent from the client to the server. It provides
 * a structure for handling the data buffer and the packet's channel name,
 * as well as methods for sending and receiving the packet.
 * **/
public abstract class PacketC2S {
    /** The buffer that holds the packet data. **/
    private final FriendlyByteBuf buf;

    /**
     * Initializes the packet with the given buffer and registers
     * the packet to be sent from the client to the server.
     * <br>
     *
     * If you want to add new fields to the packet, you can do it here
     * and then read/write them in the receive() and send() method.
     * **/
    public PacketC2S(FriendlyByteBuf buf) {
        this.buf = buf;
        MHLibPackets.registerClientToServerPacket(this);
    }

    /** Abstract method to handle the buffer on the client side. **/
    @Environment(EnvType.CLIENT)
    public abstract void send();

    /** Abstract method to handle the packet reception on the server side. **/
    @Environment(EnvType.SERVER)
    public abstract void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender);

    /** Abstract method to get the channel name for the packet. **/
    public abstract ResourceLocation getChannelName();

    /** Getter for the packet's buffer. **/
    public FriendlyByteBuf getBuf() {
        return buf;
    }
}
