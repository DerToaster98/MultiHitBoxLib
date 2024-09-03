package de.dertoaster.multihitboxlib.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

@Mixin(ServerEntity.class)
public abstract class MixinServerEntity {

	@Shadow
	private Entity entity;

	/*
	 * TODO: Rewrite Packets for new Packet system
	 */
	@Inject(
			method = "sendDirtyEntityData()V",
			at = @At("HEAD")
	)
	private void mixinSendDirtyEntityData(CallbackInfo co) {
		if (this.entity.isMultipartEntity() && this.entity instanceof IMultipartEntity<?> ime) {
			SPacketUpdateMultipart updatePacket = new SPacketUpdateMultipart(this.entity);
			//MHLibPackets.MHLIB_NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.entity), updatePacket);
			PacketDistributor.sendToPlayersTrackingEntity(this.entity, updatePacket);
		}
	}
	
}
