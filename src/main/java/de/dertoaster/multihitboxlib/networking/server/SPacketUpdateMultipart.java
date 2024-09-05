package de.dertoaster.multihitboxlib.networking.server;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.PartEntityManager;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.PacketS2C;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.partentityimp.PartEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class SPacketUpdateMultipart extends PacketS2C {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "update_multipart");
    public Entity entity;

    public SPacketUpdateMultipart(ServerPlayer player, Entity entity) {
        super(player, PacketByteBufs.empty());
        this.entity = entity;
    }

    public SPacketUpdateMultipart(ServerPlayer player) {
        super(player, PacketByteBufs.empty());
    }

    @Override
    public void send() {
        getBuf().writeInt(entity.getId());
        PartEntity<?>[] parts = PartEntityManager.getParts(entity);
        if (parts != null) {
            getBuf().writeInt(parts.length);
            for (PartEntity<?> part : parts) {
                if (part instanceof MHLibPartEntity<?> subPart) {
                    getBuf().writeBoolean(true);
                    subPart.writeData().encode(getBuf());
                } else {
                    getBuf().writeBoolean(false);
                }
            }
        } else {
            getBuf().writeInt(0);
        }

        ServerPlayNetworking.send(getPlayer(), getChannelName(), getBuf());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        Entity ent = client.level.getEntity(buf.readInt());
        if(ent != null && PartEntityManager.isMultipartEntity(entity) && ent instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isEmpty()){
            PartEntity<?>[] parts = PartEntityManager.getParts(ent);
            if (parts == null)
                return;
            int index = 0;
            List<PartDataHolder> data = new ArrayList<>();
            int len = buf.readInt();

            for (int i = 0; i < len; i++) {
                if (buf.readBoolean()) {
                    data.add(PartDataHolder.decode(buf));
                }
            }

            for (PartEntity<?> part : parts) {
                if (part instanceof MHLibPartEntity<?> subPart) {
                    final PartDataHolder dataPart = data.get(index);
                    // Give the client some freedom...
                    if (ime.getHitboxProfile().get().trustClient()) {
                        Vec3 partPos = subPart.position();
                        Vec3 serverPos = new Vec3(dataPart.x(), dataPart.y(), dataPart.z());
                        final double dist = Math.abs(partPos.distanceToSqr(serverPos));
                        if (dist > subPart.getConfig().maxDeviationFromServer()) {
                            subPart.readData(dataPart);
                        }
                    }
                    // ... Or enforce it
                    else {
                        subPart.readData(dataPart);
                    }
                    index++;
                }
            }
        }
    }

    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }

    // Copied from https://github.com/TeamTwilight/twilightforest/blob/aa59de8ff2e9f84fe36d3da595e2cab53d4695af/src/main/java/twilightforest/network/UpdateTFMultipartPacket.java#L16
    public record PartDataHolder(double x, double y, double z, float yRot, float xRot, float width, float height, boolean fixed, boolean dirty, List<SynchedEntityData.DataValue<?>> data) {

        public void encode(FriendlyByteBuf buffer) {
            buffer.writeDouble(this.x);
            buffer.writeDouble(this.y);
            buffer.writeDouble(this.z);
            buffer.writeFloat(this.yRot);
            buffer.writeFloat(this.xRot);
            buffer.writeFloat(this.width);
            buffer.writeFloat(this.height);
            buffer.writeBoolean(this.fixed);
            buffer.writeBoolean(this.dirty);
            if (this.dirty) {
                for (SynchedEntityData.DataValue<?> datavalue : this.data) {
                    datavalue.write(buffer);
                }

                buffer.writeByte(255);
            }
        }

        static PartDataHolder decode(FriendlyByteBuf buffer) {
            boolean dirty;
            return new PartDataHolder(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), dirty = buffer.readBoolean(), dirty ? unpack(
                    buffer) : null);
        }

        private static List<SynchedEntityData.DataValue<?>> unpack(FriendlyByteBuf buf) {
            List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

            int i;
            while ((i = buf.readUnsignedByte()) != 255) {
                list.add(SynchedEntityData.DataValue.read(buf, i));
            }

            return list;
        }

    }
}
