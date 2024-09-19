package de.dertoaster.multihitboxlib.mixin.geckolib;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.glibplus.MHLibExtendedGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

@Mixin(GeoRenderer.class)
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

	@Inject(method = "renderRecursively", at = @At("HEAD"), remap = false)
	default void mixinRenderRecursivelyStart(CallbackInfo ci) {
		// if (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
		this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyStart);
		// }
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

	@Overwrite
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
	}

}
