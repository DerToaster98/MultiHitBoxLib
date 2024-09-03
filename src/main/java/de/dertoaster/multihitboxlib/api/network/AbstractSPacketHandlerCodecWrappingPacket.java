package de.dertoaster.multihitboxlib.api.network;

import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public abstract class AbstractSPacketHandlerCodecWrappingPacket<T extends Object, P extends AbstractSPacketCodecWrappingPacket<T, ?>> implements IMHLibCustomPacketHandler<P> {

	@Override
	public void handleClient(P data, ClientPayloadContext context) {
		context.enqueueWork(() -> {
			final Player sender = ClientOnlyMethods.getClientPlayer();
			final Level world = ClientOnlyMethods.getWorld();
			if (world == null || sender == null) {
				return;
				//TODO: Log error
			}
			this.execHandlePacket(data, context, world, sender);
		});
	}
	
	@Override
	public void handleServer(P data, ServerPayloadContext context) {
		context.enqueueWork(() -> {
			final Player sender = context.player();
			Level world = null;
			if (sender != null) {
				world = sender.level();
			}
			this.execHandlePacket(data, context, world, sender);
		});
	}
	
	protected abstract void execHandlePacket(P packet, IPayloadContext context, Level world, Player sender);
}
