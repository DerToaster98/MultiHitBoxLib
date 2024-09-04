package de.dertoaster.multihitboxlib.api.network;

import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public interface IMHLibCustomPacketHandler<T extends IMHLibCustomPacketPayload<?>> extends IPayloadHandler<T> {

    // Clientside => C
    public default void handle(final T data, final IPayloadContext context) {
        if (context instanceof ClientPayloadContext ctxCli) {
            handleClient(data, ctxCli);
        } else if (context instanceof ServerPayloadContext ctxSrv) {
            handleServer(data, ctxSrv);
        } else {
            handleUnspecified(data, context);
        }
    }

    public void handleClient(final T data, final ClientPayloadContext context);

    public void handleServer(final T data, final ServerPayloadContext context);

    // This should NEVER enter
    public default void handleUnspecified(final T data, final IPayloadContext context) {
        throw new IllegalStateException("Context is neither client or server context!");
    }

}
