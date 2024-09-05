package de.dertoaster.multihitboxlib.mixin.entity;

import de.dertoaster.multihitboxlib.PartEntityManager;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerEntity.class)
public abstract class MixinServerEntity {
    @Unique
    public ServerEntity serverEntity = (ServerEntity) (Object) this;

    @Final
    @Shadow
    private Entity entity;

    @Inject(
            method = "sendDirtyEntityData()V",
            at = @At("HEAD")
    )
    private void mixinSendDirtyEntityData(CallbackInfo co) {
        if (PartEntityManager.isMultipartEntity(entity) && this.entity instanceof IMultipartEntity<?> ime) {
            // TODO: Fix this - fabric packet
            //SPacketUpdateMultipart updatePacket = new SPacketUpdateMultipart(serverEntity, this.entity);
            //MHLibPackets.MHLIB_NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> this.entity), updatePacket);
        }
    }
}
