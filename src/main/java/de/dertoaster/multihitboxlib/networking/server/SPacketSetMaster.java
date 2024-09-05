package de.dertoaster.multihitboxlib.networking.server;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.PacketS2C;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class SPacketSetMaster extends PacketS2C {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "set_master");
    private final int entityID;
    private final UUID masterUUID;


    public SPacketSetMaster(ServerPlayer player, final int entityID, final UUID masterUUID) {
        super(player, PacketByteBufs.empty());
        this.entityID = entityID;
        this.masterUUID = masterUUID;
    }

    public SPacketSetMaster(ServerPlayer player) {
        super(player, PacketByteBufs.empty());
        this.entityID = -1;
        this.masterUUID = null;

    }

    @Override
    public void send() {
        getBuf().writeInt(entityID);
        getBuf().writeBoolean(masterUUID != null);
        if(masterUUID != null) {
            getBuf().writeUUID(masterUUID);
        }
        ServerPlayNetworking.send(getPlayer(), getChannelName(), getBuf());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final int entityID = buf.readInt();

        final Entity entity = client.level.getEntity(entityID);
        if(entity != null && entity instanceof IMultipartEntity<?> imp) {
            // Entity does not want synching, so we won't sync any data to it
            if(!imp.syncWithModel()) {
                return;
            }
            imp.processSetMasterPacket(buf);
        }
    }


    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
