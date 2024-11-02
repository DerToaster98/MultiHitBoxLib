package de.dertoaster.multihitboxlib.mixin.azurelib;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.dertoaster.multihitboxlib.api.IMHLibExtendedRenderLayer;
import de.dertoaster.multihitboxlib.client.azurelib.renderlayer.AzurelibBoneInformationCollectorLayer;
import mod.azure.azurelib.common.api.client.renderer.GeoReplacedEntityRenderer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;

@Mixin(GeoReplacedEntityRenderer.class)
public abstract class MixinGeoReplacedEntityRenderer {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject(
			method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lmod/azure/azurelib/common/api/client/model/GeoModel;Lmod/azure/azurelib/core/animatable/GeoAnimatable;)V",
			at = @At("TAIL")
	)
	private void mixinConstructor(CallbackInfo ci) {
		GeoReplacedEntityRenderer self = (GeoReplacedEntityRenderer)(Object)this;
		self.addRenderLayer(new AzurelibBoneInformationCollectorLayer(self));
	}

	@Unique
	private void _mhlib_callLayers(final Consumer<IMHLibExtendedRenderLayer> runPerLayer) {
		GeoRenderer self = (GeoRenderer) this;
		for (Object layerGeo : self.getRenderLayers()) {
			if (layerGeo instanceof IMHLibExtendedRenderLayer mhlibExtension) {
				runPerLayer.accept(mhlibExtension);
			}
		}
	}

	@Inject(method = "renderRecursively", at = @At("HEAD"), remap = false)
	private void mixinRenderRecursivelyStart(CallbackInfo ci) {
		this._mhlib_callLayers(IMHLibExtendedRenderLayer::onRenderRecursivelyStart);
	}

	@Inject(method = "renderRecursively", at = @At("TAIL"), remap = false)
	private void mixinRenderRecursivelyEnd(CallbackInfo ci) {
		this._mhlib_callLayers(IMHLibExtendedRenderLayer::onRenderRecursivelyEnd);
	}
	
}
