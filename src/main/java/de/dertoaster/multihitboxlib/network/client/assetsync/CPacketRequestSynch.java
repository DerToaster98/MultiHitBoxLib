package de.dertoaster.multihitboxlib.network.client.assetsync;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public record CPacketRequestSynch() implements IMHLibCustomPacketPayload<CPacketRequestSynch> {

	public CPacketRequestSynch() {

	}

	public static final StreamCodec<FriendlyByteBuf, CPacketRequestSynch> STREAM_CODEC = StreamCodec.unit(new CPacketRequestSynch());

	@Override
	public StreamCodec<FriendlyByteBuf, CPacketRequestSynch> getStreamCodec() {
		return STREAM_CODEC;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.C2S_REQUEST_SYNCH;
	}
}
