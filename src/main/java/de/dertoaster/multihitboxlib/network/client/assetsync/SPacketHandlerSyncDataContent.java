package de.dertoaster.multihitboxlib.network.client.assetsync;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSyncDataContent;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public class SPacketHandlerSyncDataContent implements IMHLibCustomPacketHandler<SPacketSyncDataContent> {
    @Override
    public void handleClient(SPacketSyncDataContent data, ClientPayloadContext context) {
        context.enqueueWork(() -> {
            AssetEnforcement.handlePacketData(data.payload());
        });
    }

    @Override
    public void handleServer(SPacketSyncDataContent data, ServerPayloadContext context) {
        // Ignore...
    }
}
