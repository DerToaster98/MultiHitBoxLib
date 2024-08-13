package de.dertoaster.multihitboxlib.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IMHLibCustomPacketPayload<T extends CustomPacketPayload> extends CustomPacketPayload {

    public CustomPacketPayload.Type<T> getType();
    public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();

}
