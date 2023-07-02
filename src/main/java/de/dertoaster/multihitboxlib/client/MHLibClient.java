package de.dertoaster.multihitboxlib.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.event.client.PartRendererRegistrationEvent;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MHLibClient {
	
	protected static final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> ENTITY_PART_RENDERER_PRODUCERS = new ConcurrentHashMap<>();
	protected static final Map<Class<? extends MHLibPartEntity<?>>, EntityRenderer<? extends MHLibPartEntity<?>>> ENTITY_PART_RENDERERS = new ConcurrentHashMap<>();

	protected static void registerEntityPartRenderer(final Class<? extends MHLibPartEntity<?>> partClass, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>> rendererFactory) {
		ENTITY_PART_RENDERER_PRODUCERS.put(partClass, rendererFactory);
	}
	
	public static <R extends EntityRenderer<? extends MHLibPartEntity<?>>, P extends MHLibPartEntity<?>> EntityRenderer<? extends MHLibPartEntity<?>> getRendererFor(MHLibPartEntity<?> cpe, EntityRenderDispatcher entityRenderDispatcher) {
		return ENTITY_PART_RENDERERS.computeIfAbsent((Class<? extends MHLibPartEntity<?>>) cpe.getClass(), partClass -> {
			Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>> constructor = null;
			for(Entry<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> entry : ENTITY_PART_RENDERER_PRODUCERS.entrySet()) {
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
		
		// Fire part renderer event and collect
		final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map = new HashMap<>();
		PartRendererRegistrationEvent registrationEvent = new PartRendererRegistrationEvent(map);
		MinecraftForge.EVENT_BUS.post(registrationEvent);
		if (map != null) {
			map.entrySet().forEach(entry -> registerEntityPartRenderer(entry.getKey(), entry.getValue()));
		}
	}

}
