package de.dertoaster.multihitboxlib.network.server.assetsync;

import de.dertoaster.multihitboxlib.api.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.network.client.assetsync.CPacketRequestSynch;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public class CPacketHandlerRequestSynch extends AbstractPacketHandler<CPacketRequestSynch> {

	@Override
	protected void execHandlePacket(CPacketRequestSynch packet, Supplier<IPayloadContext> context, Level world, Player player) {
		if (player instanceof ServerPlayer sp) {
			AssetEnforcement.sendSynchData(sp);
		}
	}

}
