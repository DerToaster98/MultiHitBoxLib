package de.dertoaster.multihitboxlib.mixin.minecraft.client;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.entity.IOrientableHitbox;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.entity.PartEntity;

@Mixin(EntityRenderDispatcher.class)
public abstract class MixinEntityRenderDispatcher {

	// Mixin into the second call to renderHitbox, which is used to draw the subpart hitboxes 
	@Inject(
			method = "renderHitbox",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;DDDDDDFFFFFFF)V",
					ordinal = 2
			),
			locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void mixinDrawHitbox(
			PoseStack pPoseStack, 
			VertexConsumer pBuffer, 
			Entity pEntity, 
			float pPartialTicks,
			// Local variables
			PartEntity<?> enderdragonpart,
			// CallbackInfo
			CallbackInfo ci
	) {
		if (enderdragonpart != null && enderdragonpart instanceof IOrientableHitbox ioh) {
			// Translate to center
			pPoseStack.translate(ioh.getCenterOffset().x, ioh.getCenterOffset().y, ioh.getCenterOffset().z);
			// Rotate!
			Quaternionf quat = new Quaternionf().rotateXYZ(ioh.getRotationX(), ioh.getRotationY(), ioh.getRotationZ()); 
			pPoseStack.mulPose(quat);
		}
	}
	
}
