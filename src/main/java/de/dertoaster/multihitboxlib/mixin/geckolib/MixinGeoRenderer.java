package de.dertoaster.multihitboxlib.mixin.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.glibplus.MHLibExtendedGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(GeoRenderer.class)
public abstract class MixinGeoRenderer {

    @Unique
    private void _mhlib_callLayers(final Consumer<MHLibExtendedGeoLayer> runPerLayer) {
        GeoRenderer self = (GeoRenderer)this;
        for (Object layerGeo : self.getRenderLayers()) {
            if (layerGeo instanceof MHLibExtendedGeoLayer mhlibExtension) {
                runPerLayer.accept(mhlibExtension);
            }
        }
    }

    @Inject(
            method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At("HEAD"),
            remap = false
    )
    private void mixinRenderRecursivelyStart(PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                                             float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
            this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyStart);
        }
    }

    @Inject(
            method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            at = @At("TAIL"),
            remap = false
    )
    private void mixinRenderRecursivelyEnd(PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                                           float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (animatable != null && animatable instanceof Entity entity && entity.isMultipartEntity() && animatable instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
            this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyEnd);
        }
    }

}
