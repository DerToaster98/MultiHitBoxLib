package de.dertoaster.multihitboxlib.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IMHLibCustomPacketPayload<T extends CustomPacketPayload> extends CustomPacketPayload {

    public StreamCodec<FriendlyByteBuf, T> getStreamCodec();
    
    public default Type<T> _castType() {
    	return (Type<T>) this.type();
    }

}
