package de.dertoaster.multihitboxlib.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.event.client.PartRendererRegistrationEvent;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MHLibClient {
	
	protected static final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> ENTITY_PART_RENDERER_PRODUCERS = new ConcurrentHashMap<>();
	protected static final Map<Class<? extends MHLibPartEntity<?>>, EntityRenderer<? extends MHLibPartEntity<?>>> ENTITY_PART_RENDERERS = new ConcurrentHashMap<>();

	protected static void registerEntityPartRenderer(final Class<? extends MHLibPartEntity<?>> partClass, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>> rendererFactory) {
		ENTITY_PART_RENDERER_PRODUCERS.put(partClass, rendererFactory);
	}
	
	@SuppressWarnings("unchecked")
	public static <R extends EntityRenderer<? extends MHLibPartEntity<?>>, P extends MHLibPartEntity<?>> EntityRenderer<? extends MHLibPartEntity<?>> getRendererFor(MHLibPartEntity<?> cpe, EntityRenderDispatcher entityRenderDispatcher) {
		return ENTITY_PART_RENDERERS.computeIfAbsent((Class<? extends MHLibPartEntity<?>>) cpe.getClass(), partClass -> {
			Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>> constructor = null;
			for(Map.Entry<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> entry : ENTITY_PART_RENDERER_PRODUCERS.entrySet()) {
				if(entry.getKey().equals(partClass)) {
					constructor = entry.getValue();
					break;
				} else if(partClass.isAssignableFrom(entry.getKey())) {
					constructor = entry.getValue();
				}
			}
			if(constructor != null) {
				return constructor.apply(entityRenderDispatcher);
			}
			return null;
		});
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		EntityRenderEventHandlerCommonLogic.registerRelevantEventListeners(NeoForge.EVENT_BUS);
		
		// Fire part renderer event and collect
		final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map = new HashMap<>();
		PartRendererRegistrationEvent registrationEvent = new PartRendererRegistrationEvent(map);
		NeoForge.EVENT_BUS.post(registrationEvent);
		if (map != null) {
			map.entrySet().forEach(entry -> registerEntityPartRenderer(entry.getKey(), entry.getValue()));
		}
	}
	
}
