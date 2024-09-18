package de.dertoaster.multihitboxlib.client.geckolib;

import de.dertoaster.multihitboxlib.api.glibplus.MHLibExtendedGeoLayer;
import de.dertoaster.multihitboxlib.client.EntityRenderEventHandlerCommonLogic;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Unique;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.function.Consumer;

public class GeckolibEntityRenderEventHandler extends EntityRenderEventHandlerCommonLogic {

	static void callLayers(GeoRenderer<?> renderer, Consumer<MHLibExtendedGeoLayer<?>> runPerLayer) {
		for(GeoRenderLayer<?> layerGeo : renderer.getRenderLayers()) {
			if (layerGeo instanceof MHLibExtendedGeoLayer mhlibExtension) {
				runPerLayer.accept(mhlibExtension);
			}
		}
	}

	public static void onPostRenderEntity(GeoRenderEvent.Entity.Post event) {
		Entity animatable = event.getEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), animatable);
		performGlibLogic(event.getRenderer(), animatable);
		if (!event.getEntity().isMultipartEntity()) {
			return;
		}
		callLayers(event.getRenderer(), MHLibExtendedGeoLayer::onPostRender);
	}

	public static void onPreRenderEntity(GeoRenderEvent.Entity.Pre event) {
		if (!event.getEntity().isMultipartEntity()) {
			return;
		}
		callLayers(event.getRenderer(), MHLibExtendedGeoLayer::onPreRender);
	}

	public static void onPreRenderReplacedEntity(GeoRenderEvent.ReplacedEntity.Pre event) {
		if (!event.getReplacedEntity().isMultipartEntity()) {
			return;
		}
		callLayers(event.getRenderer(), MHLibExtendedGeoLayer::onPreRender);
	}

	public static void onPostRenderReplacedEntity(GeoRenderEvent.ReplacedEntity.Post event) {
		Entity animatable = event.getReplacedEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), animatable);
		if (!animatable.isMultipartEntity()) {
			return;
		}
		callLayers(event.getRenderer(), MHLibExtendedGeoLayer::onPostRender);
	}
	
	private static void performGlibLogic(GeoEntityRenderer<?> geoRenderer, Entity entitybeingRenderer) {
		for(GeoRenderLayer<?> gle : geoRenderer.getRenderLayers()) {
			if(gle instanceof IBoneInformationCollectorLayerCommonLogic<?> bicl) {
				bicl.onPostRender(entitybeingRenderer);
			}
		}
	}

}
