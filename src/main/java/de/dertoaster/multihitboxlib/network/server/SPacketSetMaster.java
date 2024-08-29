package de.dertoaster.multihitboxlib.network.server;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import de.dertoaster.multihitboxlib.util.UtilityCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public record SPacketSetMaster(int entityID, UUID masterUUID) implements IMHLibCustomPacketPayload<SPacketSetMaster> {

	public <T extends IMultipartEntity<?>> SPacketSetMaster(final T entity) {
		this(retrieveEntityID(entity), retrieveMasterUUID(entity));
	}

	protected static <T extends IMultipartEntity<?>> int retrieveEntityID(final T entity) {
		if (entity instanceof Entity entityTmp) {
			return ((Entity) entity).getId();
		} else {
			throw new IllegalStateException("entity is a instance of IMultipartEntity that is not implemented on a entity!");
		}
	}

	protected static <T extends IMultipartEntity<?>> UUID retrieveMasterUUID(final T entity) {
		return entity.getMasterUUID();
	}
	
	public SPacketSetMaster() {
		this(-1, UUID.randomUUID());
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, SPacketSetMaster> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			SPacketSetMaster::entityID,
			ByteBufCodecs.fromCodec(UtilityCodecs.UUID_STRING_CODEC),
			SPacketSetMaster::masterUUID,
			SPacketSetMaster::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, SPacketSetMaster> getStreamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.S2C_SET_MASTER;
	}
}
