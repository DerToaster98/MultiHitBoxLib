package de.dertoaster.multihitboxlib.mixin.geckolib;

import de.dertoaster.multihitboxlib.api.glibplus.MHLibExtendedGeoLayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.dertoaster.multihitboxlib.client.geckolib.renderlayer.GeckolibBoneInformationCollectorLayer;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;

import java.util.function.Consumer;

@Mixin(value = GeoReplacedEntityRenderer.class, priority = Integer.MAX_VALUE)
public abstract class MixinGeoReplacedEntityRenderer<E extends Entity, T extends GeoAnimatable> extends EntityRenderer<E> implements GeoRenderer<T> {

	protected MixinGeoReplacedEntityRenderer(EntityRendererProvider.Context pContext) {
		super(pContext);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Inject(
			method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lsoftware/bernie/geckolib/model/GeoModel;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;)V",
			at = @At("TAIL")
			)
	private void mixinConstructor(CallbackInfo ci) {
		GeoReplacedEntityRenderer self = (GeoReplacedEntityRenderer)(Object)this;
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
