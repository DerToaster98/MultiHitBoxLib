package de.dertoaster.multihitboxlib.client.azurelib;

import de.dertoaster.multihitboxlib.client.EntityRenderEventHandlerCommonLogic;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import mod.azure.azurelib.event.GeoRenderEntityEvent;
import mod.azure.azurelib.event.GeoRenderEvent;
import mod.azure.azurelib.event.GeoRenderReplacedEntityEvent;
import mod.azure.azurelib.event.NeoForgeGeoRenderPhaseEvent;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.world.entity.Entity;

public class AzurelibEntityRenderEventHandler extends EntityRenderEventHandlerCommonLogic {

	public static void onPostRenderEntity(NeoForgeGeoRenderPhaseEvent.NeoForgeGeoRenderEvent event) {
		if (event.getGeoRenderEvent() == (GeoRenderReplacedEntityEvent.Post) event.getGeoRenderEvent())
			return;
		GeoRenderEntityEvent.Pre azureevent = (GeoRenderEntityEvent.Pre) event.getGeoRenderEvent();
		Entity animatable = azureevent.getEntity();
		performCommonLogic(azureevent.getPoseStack(), azureevent.getRenderer(), azureevent.getBufferSource(), azureevent.getPartialTick(), azureevent.getPackedLight(), animatable);
		performGlibLogic(azureevent.getRenderer(), animatable);
	}

	public static void onPostRenderReplacedEntity(NeoForgeGeoRenderPhaseEvent.NeoForgeGeoRenderEvent event) {
		if (event.getGeoRenderEvent() == (GeoRenderEntityEvent.Post) event.getGeoRenderEvent())
			return;
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
