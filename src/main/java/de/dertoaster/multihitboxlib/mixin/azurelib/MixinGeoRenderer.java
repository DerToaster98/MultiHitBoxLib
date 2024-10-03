package de.dertoaster.multihitboxlib.mixin.azurelib;

import de.dertoaster.multihitboxlib.api.alibplus.MHLibExtendedGeoLayer;
import mod.azure.azurelib.renderer.GeoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = GeoRenderer.class, priority = Integer.MAX_VALUE - 1)
public interface MixinGeoRenderer {
	// TODO: Fix, this does not work yet

	@Unique
	default void _mhlib_callLayers(final Consumer<MHLibExtendedGeoLayer> runPerLayer) {
		GeoRenderer self = (GeoRenderer) this;
		for (Object layerGeo : self.getRenderLayers()) {
			if (layerGeo instanceof MHLibExtendedGeoLayer mhlibExtension) {
				runPerLayer.accept(mhlibExtension);
			}
		}
	}

	//renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/Object;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V
	@Inject(method = "renderRecursively", at = @At("HEAD"), remap = false)
	default void mixinRenderRecursivelyStart(/*PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel mode, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha,*/ CallbackInfo ci) {
		System.exit(-500);
		//if (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
			this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyStart);
		//}
	}

	@Inject(method = "renderRecursively", at = @At("TAIL"), remap = false)
	default void mixinRenderRecursivelyEnd(/*
											 * PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int
											 * packedOverlay, float red, float green, float blue, float alpha,
											 */ CallbackInfo ci) {
		// if (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
		this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyEnd);
		// }
	}

	/*@Overwrite
	default void renderRecursively(PoseStack poseStack, GeoAnimatable animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		boolean iNeedToCallMHLIB = (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent());
				
		if (iNeedToCallMHLIB) {
			this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyStart);
		}
		
		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, bone);
		((GeoRenderer)this).renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		if (!isReRender)
			((GeoRenderer)this).applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

		((GeoRenderer)this).renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
		
		if (iNeedToCallMHLIB) {
			this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyEnd);
		}
	}*/

}
