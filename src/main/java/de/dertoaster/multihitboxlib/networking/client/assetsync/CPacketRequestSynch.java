package de.dertoaster.multihitboxlib.networking.client.assetsync;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.network.PacketC2S;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class CPacketRequestSynch extends PacketC2S {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "request_synch");

    public CPacketRequestSynch() {
        super(PacketByteBufs.empty());
    }

    @Override
    public void send() {

        ClientPlayNetworking.send(getChannelName(), getBuf());
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        AssetEnforcement.sendSynchData(player);
    }

    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
