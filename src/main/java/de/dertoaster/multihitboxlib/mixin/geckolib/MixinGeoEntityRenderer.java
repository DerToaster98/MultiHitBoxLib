package de.dertoaster.multihitboxlib.mixin.geckolib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dertoaster.multihitboxlib.api.glibplus.MHLibExtendedGeoLayer;
import de.dertoaster.multihitboxlib.client.geckolib.renderlayer.GeckolibBoneInformationCollectorLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.function.Consumer;

@Mixin(value = GeoEntityRenderer.class, priority = Integer.MAX_VALUE)
public abstract class MixinGeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {

	protected MixinGeoEntityRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject(
			method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lsoftware/bernie/geckolib/model/GeoModel;)V",
			at = @At("TAIL")
			)
	private void mixinConstructor(CallbackInfo ci) {
		GeoEntityRenderer self = (GeoEntityRenderer)(Object)this;
		self.addRenderLayer(new GeckolibBoneInformationCollectorLayer(self));
	}

	@Unique
	private void _mhlib_callLayers(final Consumer<MHLibExtendedGeoLayer> runPerLayer) {
		GeoRenderer self = (GeoRenderer) this;
		for (Object layerGeo : self.getRenderLayers()) {
			if (layerGeo instanceof MHLibExtendedGeoLayer mhlibExtension) {
				runPerLayer.accept(mhlibExtension);
			}
		}
	}

	@Inject(method = "renderRecursively", at = @At("HEAD"), remap = false)
	private void mixinRenderRecursivelyStart(CallbackInfo ci) {
		this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyStart);
	}

	@Inject(method = "renderRecursively", at = @At("TAIL"), remap = false)
	private void mixinRenderRecursivelyEnd(CallbackInfo ci) {
		this._mhlib_callLayers(MHLibExtendedGeoLayer::onRenderRecursivelyEnd);
	}

}
