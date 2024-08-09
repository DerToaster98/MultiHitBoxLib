package de.dertoaster.multihitboxlib.network.server;

import java.util.UUID;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class SPacketSetMaster extends AbstractPacket<SPacketSetMaster> {

	private final int entityID;
	private final UUID masterUUID;
	
	public <T extends IMultipartEntity<?>> SPacketSetMaster(final T entity) {
		if (entity instanceof Entity entityTmp) {
			this.entityID = ((Entity) entity).getId();
		} else {
			throw new IllegalStateException("entity is a instance of IMultipartEntity that is not implemented on a entity!");
		}
		this.masterUUID = entity.getMasterUUID();
	}
	
	public SPacketSetMaster() {
		this.entityID = -1;
		this.masterUUID = null;
		
	}
	
	protected SPacketSetMaster(final int id, final UUID masterID) {
		this.entityID = id;
		this.masterUUID = masterID;
	}
	
	@Override
	public Class<SPacketSetMaster> getPacketClass() {
		return SPacketSetMaster.class;
	}

	@Override
	public SPacketSetMaster fromBytes(FriendlyByteBuf buffer) {
		int id = buffer.readInt();
		if(buffer.readBoolean()) {
			return new SPacketSetMaster(id, buffer.readUUID());
		} else {
			return new SPacketSetMaster(id, null);
		}
	}

	@Override
	public void toBytes(SPacketSetMaster packet, FriendlyByteBuf buffer) {
		buffer.writeInt(packet.getEntityID());
		buffer.writeBoolean(packet.getMasterUUID() != null);
		if(packet.getMasterUUID() != null) {
			buffer.writeUUID(packet.getMasterUUID());
		}
		
	}
	
	public UUID getMasterUUID() {
		return masterUUID;
	}
	
	public int getEntityID() {
		return entityID;
	}

}
