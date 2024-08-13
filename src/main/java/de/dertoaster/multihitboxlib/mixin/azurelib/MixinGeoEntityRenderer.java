package de.dertoaster.multihitboxlib.mixin.azurelib;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.dertoaster.multihitboxlib.client.azurelib.renderlayer.AzurelibBoneInformationCollectorLayer;

@Mixin(GeoEntityRenderer.class)
public abstract class MixinGeoEntityRenderer {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject(
			method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lmod/azure/azurelib/common/api/client/model/GeoModel;)V",
			at = @At("TAIL")
	)
	private void mixinConstructor(CallbackInfo ci) {
		GeoEntityRenderer self = (GeoEntityRenderer)(Object)this;
		self.addRenderLayer(new AzurelibBoneInformationCollectorLayer(self));
	}
	
}
