package de.dertoaster.multihitboxlib.network.client.assetsync;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.api.network.AbstractSPacketHandlerCodecWrappingPacket;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSynchAssets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public class SPacketHandlerSynchAssets extends AbstractSPacketHandlerCodecWrappingPacket<SynchDataContainer, SPacketSynchAssets>{

	@Override
	protected void execHandlePacket(SPacketSynchAssets packet, Supplier<Context> context, Level world, Player sender) {
		AssetEnforcement.handlePacketData(packet.getData());
	}

}
