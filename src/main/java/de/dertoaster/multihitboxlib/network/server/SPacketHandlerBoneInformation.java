package de.dertoaster.multihitboxlib.network.server;

import java.util.UUID;
import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class SPacketHandlerBoneInformation extends AbstractPacketHandler<CPacketBoneInformation> {

	@Override
	protected void execHandlePacket(CPacketBoneInformation packet, Supplier<Context> context, Level world, Player player) {
		if (!(world instanceof ServerLevel && player instanceof ServerPlayer)) {
			// Illegal side, ignore
			return;
		}
		final UUID senderID = player.getUUID();
		final int entityID = packet.getEntityID();
		
		final Entity entity = world.getEntity(entityID);
		if(entity != null && entity instanceof IMultipartEntity<?> imp) {
			// Entity does not want synching, so we won't sync any data to it
			if(!imp.syncWithModel()) {
				return;
			}
			if(!senderID.equals(imp.getMasterUUID())) {
				// UUIDs do not match => warn
				return;
			}
			imp.processBoneInformation(packet.getBoneInformation());
		}
	}

}
