package de.dertoaster.multihitboxlib.mixin.minecraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dertoaster.multihitboxlib.entity.IOrientableHitbox;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.entity.PartEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

	// Mixin into the second call to renderHitbox, which is used to draw the subpart hitboxes 
	@Inject(
			method = "renderHitbox",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V",
					ordinal = 1
			),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private static void mixinDrawHitbox(
            PoseStack poseStack, VertexConsumer buffer, Entity p_entity, float red, float green, float blue, float alpha, CallbackInfo ci, AABB aabb, double d0, double d1, double d2, PartEntity[] var14, int var15, int var16, PartEntity enderdragonpart, double d3, double d4, double d5
            // CallbackInfo
            // Local variables
    ) {
		if (enderdragonpart != null && enderdragonpart instanceof IOrientableHitbox ioh) {
			// Translate to center
			poseStack.translate(ioh.getCenterOffset().x, ioh.getCenterOffset().y, ioh.getCenterOffset().z);
			// Rotate!
			Quaternionf quat = new Quaternionf().rotateXYZ(ioh.getRotationX(), ioh.getRotationY(), ioh.getRotationZ());
			poseStack.mulPose(quat);
		}
	}
	
}
