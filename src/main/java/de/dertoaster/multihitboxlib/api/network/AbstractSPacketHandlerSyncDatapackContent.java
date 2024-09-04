package de.dertoaster.multihitboxlib.api.network;

import java.util.Map;

import javax.annotation.Nullable;

import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public abstract class AbstractSPacketHandlerSyncDatapackContent<C extends Object, P extends AbstractSPacketSyncDatapackContent<C, ?>> implements IMHLibCustomPacketHandler<P> {

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
	
	/*
	 * Params:
	 * packet: The packet
	 * context: Network context
	 * world: Optional, set when player is not null or the packet is received clientside, then it is the currently opened world
	 * player: Either the sender of the packet or the local player. Is null for packets recepted during login
	 */
	protected void execHandlePacket(P packet, IPayloadContext context, @Nullable Level world, @Nullable Player player) {
		for (Map.Entry<ResourceLocation, C> entry : packet.getData().entrySet()) {
			packet.consumer().accept(entry.getKey(), entry.getValue());
		}
	}

}
