package de.dertoaster.multihitboxlib.network.client.assetsync;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.api.network.AbstractSPacketHandlerCodecWrappingPacket;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSynchAssets;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public class SPacketHandlerSynchAssets extends AbstractSPacketHandlerCodecWrappingPacket<SynchDataContainer, SPacketSynchAssets>{

	@Override
	protected void execHandlePacket(SPacketSynchAssets packet, Supplier<Context> context, Level world, Player sender) {
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> AssetEnforcement.handlePacketData(packet.getData()));
	}

}
