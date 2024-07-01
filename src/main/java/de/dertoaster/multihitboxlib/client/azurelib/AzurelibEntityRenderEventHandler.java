package de.dertoaster.multihitboxlib.client.azurelib;

import de.dertoaster.multihitboxlib.client.EntityRenderEventHandlerCommonLogic;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.common.api.common.event.GeoRenderEntityEvent;
import mod.azure.azurelib.common.api.common.event.GeoRenderReplacedEntityEvent;
import mod.azure.azurelib.neoforge.event.NeoForgeGeoRenderPhaseEvent;
import net.minecraft.world.entity.Entity;

public class AzurelibEntityRenderEventHandler extends EntityRenderEventHandlerCommonLogic {
	
	public static void onPostRenderEntity(NeoForgeGeoRenderPhaseEvent.NeoForgeGeoRenderEvent event) {
		GeoRenderEntityEvent.Post azureevent = (GeoRenderEntityEvent.Post) event.getGeoRenderEvent();
		Entity animatable = azureevent.getEntity();
		performCommonLogic(azureevent.getPoseStack(), azureevent.getRenderer(), azureevent.getBufferSource(), azureevent.getPartialTick(), azureevent.getPackedLight(), animatable);
		performGlibLogic(azureevent.getRenderer(), animatable);
	}
	
	public static void onPostRenderReplacedEntity(NeoForgeGeoRenderPhaseEvent.NeoForgeGeoRenderEvent event) {
		GeoRenderReplacedEntityEvent.Post azureevent = (GeoRenderReplacedEntityEvent.Post) event.getGeoRenderEvent();
		Entity animatable = azureevent.getReplacedEntity();
		performCommonLogic(azureevent.getPoseStack(), azureevent.getRenderer(), azureevent.getBufferSource(), azureevent.getPartialTick(), azureevent.getPackedLight(), animatable);
	}
	
	private static void performGlibLogic(GeoEntityRenderer<?> geoRenderer, Entity entitybeingRenderer) {
		for(GeoRenderLayer<?> gle : geoRenderer.getRenderLayers()) {
			if(gle instanceof IBoneInformationCollectorLayerCommonLogic<?> bicl) {
				bicl.onPostRender(entitybeingRenderer);
			}
		}
	}

}
