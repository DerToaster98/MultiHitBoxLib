package de.dertoaster.multihitboxlib.network.client;

import com.mojang.serialization.Codec;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public record CPacketBoneInformation(
		int entityID,
		Map<String, BoneInformation> boneInformation
) implements IMHLibCustomPacketPayload<CPacketBoneInformation> {

	// Necessary for how the packet "api" works
	public CPacketBoneInformation() {
		this(0, new HashMap<>());
	}

	CPacketBoneInformation(final int entityID, final Set<BoneInformation> boneInformation) {
		this(entityID, compileMap(boneInformation));
	}

	protected static Map<String, BoneInformation> compileMap(final Set<BoneInformation> boneInformation) {
		Map<String, BoneInformation> map = new Object2ObjectArrayMap<>(boneInformation.size());
		for (BoneInformation bi : boneInformation) {
			map.put(bi.name(), bi);
		}
		return map;
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, CPacketBoneInformation> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			CPacketBoneInformation::entityID,
			ByteBufCodecs.fromCodec(
					Codec.unboundedMap(Codec.STRING, BoneInformation.CODEC)
			),
			CPacketBoneInformation::boneInformation,
			CPacketBoneInformation::new
	);

	public void send() {
		PacketDistributor.sendToServer(this);
	}

	public static Builder builder(Entity entity) {
		return new Builder(entity.getId());
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, CPacketBoneInformation> getStreamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.C2S_BONE_INFORMATION;
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

}
