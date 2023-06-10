package de.dertoaster.multihitboxlib.mixin.geckolib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class MixinGeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {
	
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	
	private double rotX = 0.0D;
	private double rotY = 0.0D;
	private double rotZ = 0.0D;
	
	protected MixinGeoEntityRenderer(Context pContext) {
		super(pContext);
	}
	
	/*@Inject(
			method = "postRender("
					+ "Lcom/mojang/blaze3d/vertex/PoseStack;"
					+ "Ljava/lang/Object;"
					+ "Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;"
					+ "Lnet/minecraft/client/renderer/MultiBufferSource;"
					+ "Lcom/mojang/blaze3d/vertex/VertexConsumer;"
					+ "Z"
					+ "F"
					+ "I"
					+ "I"
					+ "F"
					+ "F"
					+ "F"
					+ "F"
					+ ")V",
			at = @At("TAIL")
	)
	private void mixinPostRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
		if (isReRender) {
			return;
		}
		if (animatable.isMultipartEntity() &&  animatable instanceof IMultipartEntity<?> ime && animatable.getParts() != null && animatable.getParts().length > 0) {
			for(PartEntity<?> part : animatable.getParts()) {
				if(part instanceof MHLibPartEntity<?> mhlpe) {
					if (mhlpe.hasCustomRenderer()) {
						// TODO: Allow custom part renderers
					} else {
						continue;
					}
				}
			}
		}
	}*/

}
