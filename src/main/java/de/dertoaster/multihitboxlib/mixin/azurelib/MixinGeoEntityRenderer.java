package de.dertoaster.multihitboxlib.mixin.azurelib;



import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.dertoaster.multihitboxlib.api.IMHLibExtendedRenderLayer;
import de.dertoaster.multihitboxlib.client.azurelib.renderlayer.AzurelibBoneInformationCollectorLayer;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;


@Mixin(value = GeoEntityRenderer.class, priority = Integer.MAX_VALUE)
public abstract class MixinGeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {

	protected MixinGeoEntityRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject(
			method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lmod/azure/azurelib/common/api/client/model/GeoModel;)V",
			at = @At("TAIL")
	)
	private void mixinConstructor(CallbackInfo ci) {
		GeoEntityRenderer self = (GeoEntityRenderer)(Object)this;
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
