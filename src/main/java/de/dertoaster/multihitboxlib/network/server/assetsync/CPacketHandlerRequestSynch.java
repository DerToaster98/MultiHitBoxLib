package de.dertoaster.multihitboxlib.network.server.assetsync;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.network.client.assetsync.CPacketRequestSynch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public class CPacketHandlerRequestSynch implements IMHLibCustomPacketHandler<CPacketRequestSynch> {

	@Override
	public void handleClient(CPacketRequestSynch data, ClientPayloadContext context) {

	}

	@Override
	public void handleServer(CPacketRequestSynch data, ServerPayloadContext context) {
		Player player = context.player();
		if (player == null) {
			return;
		}
		if (player instanceof ServerPlayer sp) {
			AssetEnforcement.sendSynchData(sp);
		}
	}
}
