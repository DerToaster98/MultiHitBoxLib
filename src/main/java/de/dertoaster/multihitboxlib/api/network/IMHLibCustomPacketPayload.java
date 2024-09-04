package de.dertoaster.multihitboxlib.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IMHLibCustomPacketPayload<T extends CustomPacketPayload> extends CustomPacketPayload {

    public StreamCodec<FriendlyByteBuf, T> getStreamCodec();
    
    @SuppressWarnings("unchecked")
	public default Type<T> _castType() {
    	try {
    		return (Type<T>) this.type();
    	} catch(ClassCastException cce) {
    		throw new IllegalStateException("Somehow, the type given to packet " + this.getClass().getName() + " does not fit to it");
    	}
    }

}
