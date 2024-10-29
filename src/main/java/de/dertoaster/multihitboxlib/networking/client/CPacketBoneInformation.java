package de.dertoaster.multihitboxlib.networking.client;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.PacketC2S;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class CPacketBoneInformation extends PacketC2S {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "bone_information");
    private int entityID;
    private Map<String, BoneInformation> boneInformation;

    public CPacketBoneInformation(final int entityID, final Set<BoneInformation> boneInformation) {
        super(PacketByteBufs.empty());
        this.entityID = entityID;
        this.boneInformation = new Object2ObjectArrayMap<>();
    }

    public CPacketBoneInformation(final int entityID, final Map<String, BoneInformation> boneInformation) {
        super(PacketByteBufs.empty());
        this.entityID = entityID;
        this.boneInformation = boneInformation;
    }

    public CPacketBoneInformation() {
        super(PacketByteBufs.empty());
    }

    @Override
    public void send() {
        getBuf().writeInt(entityID);
        getBuf().writeInt(boneInformation.values().size());
        for (BoneInformation bi : boneInformation.values()) {
            getBuf().writeJsonWithCodec(BoneInformation.CODEC, bi);
        }

        ClientPlayNetworking.send(getChannelName(), getBuf());
    }


    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final UUID senderID = player.getUUID();
        final int entityID = buf.readInt();

        final Entity entity = player.level().getEntity(entityID);
        if(entity != null && entity instanceof IMultipartEntity<?> imp) {
            // Entity does not want synching, so we won't sync any data to it
            if(!imp.syncWithModel()) {
                return;
            }
            if(!senderID.equals(imp.getMasterUUID())) {
                // UUIDs do not match => warn
                return;
            }

            int infoCount = buf.readInt();
            final Map<String, BoneInformation> infoMap = new Object2ObjectArrayMap<>(infoCount);
            while (infoCount > 0) {
                infoCount--;
                BoneInformation bi = buf.readJsonWithCodec(BoneInformation.CODEC);
                infoMap.put(bi.name(), bi);
            }
            imp.processBoneInformation(infoMap);
        }
    }

    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }

    public static <T extends Entity & IMultipartEntity<?>> Builder builder(T entity) {
        return new Builder(entity.getId());
    }

    public static class Builder {

        private final int entityID;
        private final Set<String> processedBones = new HashSet<>();
        private final Set<BoneInformation> boneInformation = new HashSet<>();

        private Optional<String> currentBoneName = Optional.empty();
        private Optional<Vec3> currentBonePos = Optional.empty();
        private Optional<Vec3> currentBoneScales = Optional.empty();
        private Optional<Vec3> currentBoneRotations = Optional.empty();
        private Optional<Boolean> currentBoneHidden = Optional.empty();

        Builder(final int entityID) {
            this.entityID = entityID;
        }

        public Builder addInfo(final String boneName) {
            if (this.processedBones.contains(boneName)) {
                throw new IllegalStateException("a bone with the name of " + boneName + " has already been added!");
            }
            if (this.currentBoneName.isPresent()) {
                this.compileAndAddBone();
            }
            this.currentBoneName = Optional.of(boneName);

            return this;
        }

        public Builder done() {
            if (this.currentBoneName.isPresent()) {
                this.compileAndAddBone();
            }
            return this;
        }

        public Builder position(final Vec3 worldPosition) {
            this.checkState();

            this.currentBonePos = Optional.ofNullable(worldPosition);

            return this;
        }

        public Builder scaling(final Vec3 currentScaling) {
            this.checkState();

            this.currentBoneScales = Optional.ofNullable(currentScaling);

            return this;
        }

        public Builder rotation(Vec3 rotation) {
            this.checkState();

            this.currentBoneRotations = Optional.ofNullable(rotation);

            return this;
        }

        public Builder hidden(final boolean hidden) {
            this.checkState();

            this.currentBoneHidden = Optional.ofNullable(hidden);

            return this;
        }

        private void checkState() {
            if (this.currentBoneName.isEmpty()) {
                throw new IllegalStateException("a bone name must be set, otherwise the state is invalid!");
            }
        }

        private void compileAndAddBone() {
            BoneInformation bi = new BoneInformation(this.currentBoneName.get(), this.currentBoneHidden.orElse(false), this.currentBonePos.orElse(Vec3.ZERO), this.currentBoneScales.orElse(BoneInformation.DEFAULT_SCALING), this.currentBoneRotations.orElse(Vec3.ZERO));
            if (!this.boneInformation.add(bi)) {
                // throw new IllegalStateException("Unable to add information for bone " + bi.name());
            }

            this.currentBoneName = Optional.empty();
            this.currentBonePos = Optional.empty();
            this.currentBoneScales = Optional.empty();
            this.currentBoneRotations = Optional.empty();
        }

        public CPacketBoneInformation build() {
            if (this.currentBoneName.isPresent()) {
                this.compileAndAddBone();
            }

            return new CPacketBoneInformation(this.entityID, this.boneInformation);
        }
    }

    public int getEntityID() {
        return this.entityID;
    }

    public Map<String, BoneInformation> getBoneInformation() {
        return this.boneInformation;
    }
}
