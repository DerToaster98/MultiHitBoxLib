package de.dertoaster.multihitboxlib.network.server;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class SPacketHandlerBoneInformation extends AbstractPacketHandler<CPacketBoneInformation>{

	@Override
	protected void execHandlePacket(CPacketBoneInformation packet, Supplier<Context> context, Level world, Player player) {
		
	}

}
