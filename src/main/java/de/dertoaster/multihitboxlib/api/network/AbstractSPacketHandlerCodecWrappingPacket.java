package de.dertoaster.multihitboxlib.api.network;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import net.minecraft.network.protocol.game.ServerPacketListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public abstract class AbstractSPacketCodecWrappingPacketHandler<T extends Object, P extends AbstractSPacketCodecWrappingPacket<T, ?>> implements IMessageHandler<P> {

	public IMessageHandler<P> cast() {
		return (IMessageHandler<P>)this;
	}
	
	@Override
	public void handlePacket(P packet, Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			Player sender = null;
			Level world = null;
			if(context.get().getNetworkManager().getPacketListener() instanceof ServerPacketListener) {
				sender = context.get().getSender();
				if(sender != null) {
					world = sender.level();
				}
			}
			//if(context.get().getNetworkManager().getPacketListener() instanceof ClientPacketListener) {
			//Otherwise it must be client?
			else {
				sender = ClientOnlyMethods.getClientPlayer();
				world = ClientOnlyMethods.getWorld();
			}
			
			
			this.execHandlePacket(packet, context, world, sender);
		});
		context.get().setPacketHandled(true);
	}

	protected abstract void execHandlePacket(P packet, Supplier<Context> context, Level world, Player sender);
}
