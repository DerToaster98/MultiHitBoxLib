package de.dertoaster.multihitboxlib.network.client;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class CPacketHandlerSetMaster extends AbstractPacketHandler<SPacketSetMaster> {

	@Override
	protected void execHandlePacket(SPacketSetMaster packet, Supplier<Context> context, Level world, Player player) {
		if (!(world instanceof ClientLevel || player instanceof AbstractClientPlayer)) {
			// Illegal side, ignore
			return;
		}
		final int entityID = packet.getEntityID();
		
		final Entity entity = world.getEntity(entityID);
		if(entity != null && entity instanceof IMultipartEntity<?> imp) {
			// Entity does not want synching, so we won't sync any data to it
			if(!imp.syncWithModel()) {
				return;
			}
			imp.processSetMasterPacket(packet);
		}
	}

}
