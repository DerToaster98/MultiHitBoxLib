package de.dertoaster.multihitboxlib.network.client;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.NetworkEvent.Context;

public class CPacketHandlerUpdateMultipart extends AbstractPacketHandler<SPacketUpdateMultipart> {

	@Override
	protected void execHandlePacket(SPacketUpdateMultipart packet, Supplier<Context> context, Level world, Player player) {
		Entity ent = world.getEntity(packet.getId());
		if (ent != null && ent.isMultipartEntity()) {
			PartEntity<?>[] parts = ent.getParts();
			if (parts == null)
				return;
			int index = 0;
			for (PartEntity<?> part : parts) {
				if (part instanceof MHLibPartEntity<?> subPart) {
					subPart.readData(packet.getData().get(index));
					index++;
				}
			}
		}
	}

}
