package de.dertoaster.multihitboxlib.network.client;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import de.dertoaster.multihitboxlib.network.AbstractPacket;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CPacketBoneInformation extends AbstractPacket<CPacketBoneInformation> {

	private int entityID;
	private Map<String, BoneInformation> boneInformation;

	// Necessary for how the packet "api" works
	public CPacketBoneInformation() {

	}

	@Override
	public Class<CPacketBoneInformation> getPacketClass() {
		return CPacketBoneInformation.class;
	}

	@Override
	public CPacketBoneInformation fromBytes(FriendlyByteBuf buffer) {
		final int entityID = buffer.readInt();
		int infoCount = buffer.readInt();
		final Map<String, BoneInformation> infoMap = new Object2ObjectArrayMap<>(infoCount);
		while (infoCount > 0) {
			infoCount--;
			BoneInformation bi = buffer.readJsonWithCodec(BoneInformation.CODEC);
			infoMap.put(bi.name(), bi);
		}

		return new CPacketBoneInformation(entityID, infoMap);
	}

	@Override
	public void toBytes(CPacketBoneInformation packet, FriendlyByteBuf buffer) {
		buffer.writeInt(packet.entityID);
		buffer.writeInt(packet.boneInformation.values().size());
		for (BoneInformation bi : packet.boneInformation.values()) {
			buffer.writeJsonWithCodec(BoneInformation.CODEC, bi);
		}
	}

	CPacketBoneInformation(final int entityID, final Set<BoneInformation> boneInformation) {
		this.entityID = entityID;
		this.boneInformation = new Object2ObjectArrayMap<>(boneInformation.size());
		for (BoneInformation bi : boneInformation) {
			this.boneInformation.put(bi.name(), bi);
		}
	}

	CPacketBoneInformation(final int entityID, final Map<String, BoneInformation> boneInformation) {
		this.entityID = entityID;
		this.boneInformation = boneInformation;
	}

	public void send() {
		MHLibPackets.sendToServer(this);
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

			this.currentBonePos = Optional.of(worldPosition);

			return this;
		}

		public Builder scaling(final Vec3 currentScaling) {
			this.checkState();

			this.currentBoneScales = Optional.of(currentScaling);

			return this;
		}

		private void checkState() {
			if (this.currentBoneName.isEmpty()) {
				throw new IllegalStateException("a bone name must be set, otherwise the state is invalid!");
			}
		}

		private void compileAndAddBone() {
			BoneInformation bi = new BoneInformation(this.currentBoneName.get(), this.currentBonePos.get(), this.currentBoneScales.orElse(BoneInformation.DEFAULT_SCALING));
			if (!this.boneInformation.add(bi)) {
				throw new IllegalStateException("Unable to add information for bone " + bi.name());
			}

			this.currentBoneName = Optional.empty();
			this.currentBonePos = Optional.empty();
			this.currentBoneScales = Optional.empty();
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
