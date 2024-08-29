package de.dertoaster.multihitboxlib.network.client.assetsync;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public record CPacketRequestSynch() implements IMHLibCustomPacketPayload<CPacketRequestSynch> {

	public CPacketRequestSynch() {

	}

	public static final StreamCodec<RegistryFriendlyByteBuf, CPacketRequestSynch> STREAM_CODEC = StreamCodec.unit(new CPacketRequestSynch());

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, CPacketRequestSynch> getStreamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.C2S_REQUEST_SYNCH;
	}
}
