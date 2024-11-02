package de.dertoaster.multihitboxlib.network.server;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SPacketFunctionalAnimProgress(
		String wrappedControllerName, 
		int animatableOwnerId, 
		double clientUpdateTickDelta
	) implements IMHLibCustomPacketPayload<SPacketFunctionalAnimProgress> {

	public SPacketFunctionalAnimProgress() {
		this("", -1, -1);
	}

	public SPacketFunctionalAnimProgress(String wrappedControllerName, int animatableOwnerId, double clientUpdateTickDelta) {
		this.wrappedControllerName = wrappedControllerName;
		this.animatableOwnerId = animatableOwnerId;
		this.clientUpdateTickDelta = clientUpdateTickDelta;
	}

	public String getWrappedControllerName() {
		return wrappedControllerName;
	}

	public int getAnimatableOwnerId() {
		return animatableOwnerId;
	}

	public double getClientUpdateTickDelta() {
		return clientUpdateTickDelta;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return MHLibNetwork.S2C_FUNCTIONAL_ANIM_PROGRESS;
	}
	
	public static final StreamCodec<FriendlyByteBuf, SPacketFunctionalAnimProgress> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			SPacketFunctionalAnimProgress::wrappedControllerName,
			ByteBufCodecs.INT,
			SPacketFunctionalAnimProgress::animatableOwnerId,
			ByteBufCodecs.DOUBLE,
			SPacketFunctionalAnimProgress::clientUpdateTickDelta,
			SPacketFunctionalAnimProgress::new
	);

	@Override
	public StreamCodec<FriendlyByteBuf, SPacketFunctionalAnimProgress> getStreamCodec() {
		return STREAM_CODEC;
	}
	
}