package de.dertoaster.multihitboxlib.network.server;

import java.util.ArrayList;
import java.util.List;

import de.dertoaster.multihitboxlib.api.network.AbstractPacket;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;

public class SPacketUpdateMultipart extends AbstractPacket<SPacketUpdateMultipart> {

	private int id;
	private Entity entity;
	private int len;
	private final List<PartDataHolder> data = new ArrayList<>();

	public SPacketUpdateMultipart() {
		// Nothing to do here...
	}

	public SPacketUpdateMultipart(Entity entity) {
		this.entity = entity;
	}

	@Override
	public Class<SPacketUpdateMultipart> getPacketClass() {
		return SPacketUpdateMultipart.class;
	}

	@Override
	public SPacketUpdateMultipart fromBytes(FriendlyByteBuf buffer) {
		SPacketUpdateMultipart result = new SPacketUpdateMultipart();
		result.id = buffer.readInt();
		result.len = buffer.readInt();
		for (int i = 0; i < result.len; i++) {
			if (buffer.readBoolean()) {
				result.data.add(PartDataHolder.decode(buffer));
			}
		}
		return result;
	}

	@Override
	public void toBytes(SPacketUpdateMultipart packet, FriendlyByteBuf buffer) {
		buffer.writeInt(packet.entity.getId());
		PartEntity<?>[] parts = packet.entity.getParts();
		// We assume the client and server part arrays are identical, else everything will crash and burn. Don't even bother handling it.
		if (parts != null) {
			buffer.writeInt(parts.length);
			for (PartEntity<?> part : parts) {
				if (part instanceof MHLibPartEntity<?> subPart) {
					buffer.writeBoolean(true);
					subPart.writeData().encode(buffer);
				} else {
					buffer.writeBoolean(false);
				}
			}
		} else {
			buffer.writeInt(0);
		}

	}
	
	public int getId() {
		return id;
	}

	public int getLen() {
		return len;
	}

	public List<PartDataHolder> getData() {
		return data;
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
