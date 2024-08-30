package de.dertoaster.multihitboxlib.network.client;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart.PartDataHolder;
import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public class SPacketHandlerUpdateMultipart implements IMHLibCustomPacketHandler<SPacketUpdateMultipart> {

	@Override
	public void handleClient(SPacketUpdateMultipart data, ClientPayloadContext context) {
		context.enqueueWork(() -> {
			final Level world = ClientOnlyMethods.getWorld();
			Entity entity = world.getEntity(data.entityId());
			if (entity != null && entity.isMultipartEntity() && entity instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
				PartEntity<?>[] parts = entity.getParts();
				if (parts == null)
					return;
				int index = 0;
				for (PartEntity<?> part : parts) {
					if (part instanceof MHLibPartEntity<?> subPart) {
						final PartDataHolder partData = data.data().get(index);
						// Give the client some freedom...
						if (ime.getHitboxProfile().get().trustClient()) {
							Vec3 partPos = subPart.position();
							Vec3 serverPos = new Vec3(partData.x(), partData.y(), partData.z());
							final double dist = Math.abs(partPos.distanceToSqr(serverPos));
							if (dist > subPart.getConfig().maxDeviationFromServer()) {
								subPart.readData(partData);
							}
						}
						// ... Or enforce it
						else {
							subPart.readData(partData);
						}
						index++;
					}
				}
			}
		});
	}

	@Override
	public void handleServer(SPacketUpdateMultipart data, ServerPayloadContext context) {
		// Ignore
	}
}
