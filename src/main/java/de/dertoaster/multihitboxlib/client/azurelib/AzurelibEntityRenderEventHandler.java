package de.dertoaster.multihitboxlib.client.azurelib;

import de.dertoaster.multihitboxlib.client.EntityRenderEventHandlerCommonLogic;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import mod.azure.azurelib.event.GeoRenderEvent;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.world.entity.Entity;

public class AzurelibEntityRenderEventHandler extends EntityRenderEventHandlerCommonLogic {
	
	public static void onPostRenderEntity(GeoRenderEvent.Entity.Post event) {
		Entity animatable = event.getEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), animatable);
		performGlibLogic(event.getRenderer(), animatable);
	}
	
	public static void onPostRenderReplacedEntity(GeoRenderEvent.ReplacedEntity.Post event) {
		Entity animatable = event.getReplacedEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), animatable);
	}
	
	private static void performGlibLogic(GeoEntityRenderer<?> geoRenderer, Entity entitybeingRenderer) {
		for(GeoRenderLayer<?> gle : geoRenderer.getRenderLayers()) {
			if(gle instanceof IBoneInformationCollectorLayerCommonLogic<?> bicl) {
				bicl.onPostRender(entitybeingRenderer);
			}
		}
	}

}
