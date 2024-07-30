package de.dertoaster.multihitboxlib.network.client;

import de.dertoaster.multihitboxlib.api.glibplus.IExtendedGeoAnimatableEntity;
import de.dertoaster.multihitboxlib.api.glibplus.WrappedAnimationController;
import de.dertoaster.multihitboxlib.api.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.server.SPacketFunctionalAnimProgress;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Handles S2C functional animation progress (in ticks) sync by updating functional anim progress on the client every time
 * it's updated on the server. This helps ensure synchronization of functional animation metadata between the client and server, especially
 * in cases where the server is running at less than 18 TPS/is lagging.
 */
public class SPacketHandlerFunctionalAnimProgress extends AbstractPacketHandler<SPacketFunctionalAnimProgress> {

    /*
     * TODO: Rewrite Packets for new Packet system
     */
    @Override
    protected void execHandlePacket(SPacketFunctionalAnimProgress packet, Supplier<IPayloadContext> context, @Nullable Level world, @Nullable Player player) {
        if (!(world instanceof ClientLevel)) return;

        String wrappedControllerName = packet.getWrappedControllerName();
        int animatableOwnerId = packet.getAnimatableOwnerId();
        double clientUpdateTickDelta = packet.getClientUpdateTickDelta();
        Entity targetPotentialAnimatable = world.getEntity(animatableOwnerId);

        if (!(targetPotentialAnimatable instanceof IExtendedGeoAnimatableEntity) || targetPotentialAnimatable == null) return;

        IExtendedGeoAnimatableEntity targetAnimatable = (IExtendedGeoAnimatableEntity) targetPotentialAnimatable;
        WrappedAnimationController<IExtendedGeoAnimatableEntity> targetWrappedController = targetAnimatable.getWrappedControllerByName(wrappedControllerName);


    }
}
