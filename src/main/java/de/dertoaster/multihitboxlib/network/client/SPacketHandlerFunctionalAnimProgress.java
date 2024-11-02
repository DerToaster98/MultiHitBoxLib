package de.dertoaster.multihitboxlib.network.client;

import de.dertoaster.multihitboxlib.api.glibplus.IExtendedGeoAnimatableEntity;
import de.dertoaster.multihitboxlib.api.glibplus.WrappedAnimationController;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.network.server.SPacketFunctionalAnimProgress;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

/**
 * Handles S2C functional animation progress (in ticks) sync by updating functional anim progress on the client every time it's updated on the server. This helps ensure synchronization of functional animation metadata between the client and server,
 * especially in cases where the server is running at less than 18 TPS/is lagging.
 */
public class SPacketHandlerFunctionalAnimProgress implements IMHLibCustomPacketHandler<SPacketFunctionalAnimProgress> {

	@Override
	public void handleClient(SPacketFunctionalAnimProgress data, ClientPayloadContext context) {
		context.enqueueWork(() -> {
			final Player player = context.player();
			final Level world = player.level();

			if (!(world instanceof ClientLevel))
				return;

			String wrappedControllerName = data.getWrappedControllerName();
			int animatableOwnerId = data.getAnimatableOwnerId();
			double clientUpdateTickDelta = data.getClientUpdateTickDelta();
			Entity targetPotentialAnimatable = world.getEntity(animatableOwnerId);

			if (!(targetPotentialAnimatable instanceof IExtendedGeoAnimatableEntity) || targetPotentialAnimatable == null)
				return;

			IExtendedGeoAnimatableEntity targetAnimatable = (IExtendedGeoAnimatableEntity) targetPotentialAnimatable;
			WrappedAnimationController<IExtendedGeoAnimatableEntity> targetWrappedController = targetAnimatable.getWrappedControllerByName(wrappedControllerName);
		});
	}

	@Override
	public void handleServer(SPacketFunctionalAnimProgress data, ServerPayloadContext context) {
		// TODO Auto-generated method stub

	}
}
