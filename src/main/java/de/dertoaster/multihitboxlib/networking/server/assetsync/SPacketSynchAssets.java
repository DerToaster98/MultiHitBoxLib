package de.dertoaster.multihitboxlib.networking.server.assetsync;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.network.PacketS2C;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

public class SPacketSynchAssets extends PacketS2C {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "synch_assets");
    protected final SynchDataContainer data;

    public SPacketSynchAssets(ServerPlayer player) {
        super(player, PacketByteBufs.empty());
        this.data = null;
    }

    public SPacketSynchAssets(ServerPlayer player, SynchDataContainer data) {
        super(player, PacketByteBufs.empty());
        this.data = data;
    }

    protected Codec<SynchDataContainer> codec() {
        return SynchDataContainer.CODEC;
    }


    @Override
    public void send() {
        DataResult<JsonElement> dr = codec().encodeStart(JsonOps.COMPRESSED, data);
        JsonElement je = dr.getOrThrow(false, (s) -> {

        });
        if (je != null) {
            byte[] bytes = je.toString().getBytes();
            try {
                bytes = CompressionUtil.compress(bytes, Deflater.BEST_COMPRESSION, true);
                getBuf().writeBoolean(true);
                getBuf().writeByteArray(bytes);
            } catch (IOException e) {
                getBuf().writeBoolean(false);
                e.printStackTrace();
            }
        } else {
            getBuf().writeBoolean(false);
        }
        //buffer.writeJsonWithCodec(this.codec(), packet.getData());
        ServerPlayNetworking.send(getPlayer(), CHANNEL_NAME, getBuf());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (buf.readBoolean()) {
            byte[] bytes = buf.readByteArray();
            if (bytes.length > 0) {
                try {
                    bytes = CompressionUtil.decompress(bytes, true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DataFormatException e) {
                    e.printStackTrace();
                }
                JsonElement je = JsonParser.parseString(new String(bytes));
                DataResult<SynchDataContainer> dr = this.codec().parse(JsonOps.COMPRESSED, je);

                AssetEnforcement.handlePacketData(dr.getOrThrow(false, (s) -> {
                }));
            }
        }
        //T data = buffer.readJsonWithCodec(this.codec());
        //return this.createPacket(data);

    }

    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
