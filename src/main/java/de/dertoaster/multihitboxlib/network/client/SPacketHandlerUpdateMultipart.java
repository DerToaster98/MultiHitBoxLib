package de.dertoaster.multihitboxlib.network.client;

import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart.PartDataHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public class SPacketHandlerUpdateMultipart extends AbstractPacketHandler<SPacketUpdateMultipart> {

	@Override
	protected void execHandlePacket(SPacketUpdateMultipart packet, Supplier<Context> context, Level world, Player player) {
		Entity ent = world.getEntity(packet.getId());
		if (ent != null && ent.isMultipartEntity() && ent instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
			PartEntity<?>[] parts = ent.getParts();
			if (parts == null)
				return;
			int index = 0;
			for (PartEntity<?> part : parts) {
				if (part instanceof MHLibPartEntity<?> subPart) {
					final PartDataHolder data = packet.getData().get(index);
					// Give the client some freedom...
					if (ime.getHitboxProfile().get().trustClient()) {
						Vec3 partPos = subPart.position();
						Vec3 serverPos = new Vec3(data.x(), data.y(), data.z());
						final double dist = Math.abs(partPos.distanceToSqr(serverPos));
						if (dist > subPart.getConfig().maxDeviationFromServer()) {
							subPart.readData(data);
						}
					}
					// ... Or enforce it
					else {
						subPart.readData(data);
					}
					index++;
				}
			}
		}
	}

}
