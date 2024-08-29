package de.dertoaster.multihitboxlib.network.client;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public class SPacketHandlerSetMaster implements IMHLibCustomPacketHandler<SPacketSetMaster> {

	@Override
	public void handleClient(SPacketSetMaster data, ClientPayloadContext context) {
		context.enqueueWork(() -> {
			final Player player = context.player();
			if (player == null) {
				return;
			}
			final Level world = player.level();
			if (world == null) {
				return;
			}
			if (!(world instanceof ClientLevel || player instanceof AbstractClientPlayer)) {
				// Illegal side, ignore
				return;
			}
			final int entityID = data.entityID();

			final Entity entity = world.getEntity(entityID);
			if(entity != null && entity instanceof IMultipartEntity<?> imp) {
				// Entity does not want synching, so we won't sync any data to it
				if(!imp.syncWithModel()) {
					return;
				}
				imp.processSetMasterPacket(data);
			}
		}).exceptionally(e -> {
			context.disconnect(Component.translatable("mhlib.networking.s2c_set_master_failed", e.getMessage()));
			return null;
		});;
	}

	@Override
	public void handleServer(SPacketSetMaster data, ServerPayloadContext context) {

	}
}
