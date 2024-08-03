package de.dertoaster.multihitboxlib.api.network;


import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public interface IMessageHandler<T extends Object> {
	
	public void handlePacket(T packet, Supplier<IPayloadContext> context);

}
