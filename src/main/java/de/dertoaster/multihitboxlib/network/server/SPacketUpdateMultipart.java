package de.dertoaster.multihitboxlib.network.server;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.PartEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public record SPacketUpdateMultipart(
		int entityId,
		@Nullable Entity entity,
		@Nullable List<PartDataHolder> data
) implements IMHLibCustomPacketPayload<SPacketUpdateMultipart> {

	public SPacketUpdateMultipart() {
		// Nothing to do here...
		this(-1, null, null);
	}

	public SPacketUpdateMultipart(Entity entity) {
		this(entity.getId(), entity, compileList(entity));
	}

	public SPacketUpdateMultipart(FriendlyByteBuf buf) {
		this(buf.readInt(), null, new ArrayList<>());
		if (!(buf instanceof RegistryFriendlyByteBuf)) {
			throw new IllegalStateException("SPacketUpdateMultiPart can ONLY be sent on the play network channel!");
		}
		RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
		int length = regBuf.readInt();
		for (int i = 0; i < length; i++) {
			this.data.add(PartDataHolder.decode(regBuf));
		}
		// If this fails, we have a problem
		int endMarker = regBuf.readInt();
		if (endMarker != -1) {
			throw new IllegalStateException("End marker invalid!");
		}
	}

	public void write(FriendlyByteBuf buf) {
		if (!(buf instanceof RegistryFriendlyByteBuf)) {
			throw new IllegalStateException("SPacketUpdateMultiPart can ONLY be sent on the play network channel!");
		}
		RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
		if (this.entity == null)
			throw new IllegalStateException("Null Entity while encoding SPacketUpdateMultipart");
		if (this.data == null)
			throw new IllegalStateException("Null Data while encoding SPacketUpdateMultipart");
		regBuf.writeInt(this.entity.getId());
		regBuf.writeInt(this.data.size());
		for (PartDataHolder data : this.data) {
			data.encode(regBuf);
		}
		regBuf.writeInt(-1);
	}

	protected static List<PartDataHolder> compileList(final Entity entity) {
		List<PartDataHolder> result = new ArrayList<>();
		for (PartEntity<?> part : entity.getParts()) {
			if (!(part instanceof MHLibPartEntity<?>)) {
				continue;
			}
			MHLibPartEntity<?> mhLibPart = (MHLibPartEntity<?>) part;
			result.add(mhLibPart.writeData());
		}
		return result;
	}

	public static final StreamCodec<FriendlyByteBuf, SPacketUpdateMultipart> STREAM_CODEC = CustomPacketPayload.codec(SPacketUpdateMultipart::write, SPacketUpdateMultipart::new);

	@Override
	public StreamCodec<FriendlyByteBuf, SPacketUpdateMultipart> getStreamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.S2C_UPDATE_MULTIPART;
	}

	// Copied from https://github.com/TeamTwilight/twilightforest/blob/aa59de8ff2e9f84fe36d3da595e2cab53d4695af/src/main/java/twilightforest/network/UpdateTFMultipartPacket.java#L16
	public record PartDataHolder(double x, double y, double z, float yRot, float xRot, float width, float height, boolean fixed, boolean dirty, List<SynchedEntityData.DataValue<?>> data) {

		public void encode(RegistryFriendlyByteBuf buffer) {
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

		static PartDataHolder decode(RegistryFriendlyByteBuf buffer) {
			boolean dirty;
			return new PartDataHolder(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), dirty = buffer.readBoolean(), dirty ? unpack(
					buffer) : null);
		}

		private static List<SynchedEntityData.DataValue<?>> unpack(RegistryFriendlyByteBuf buf) {
			List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();

			int i;
			while ((i = buf.readUnsignedByte()) != 255) {
				list.add(SynchedEntityData.DataValue.read(buf, i));
			}

			return list;
		}

	}

}
