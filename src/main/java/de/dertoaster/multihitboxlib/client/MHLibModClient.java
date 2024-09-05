package de.dertoaster.multihitboxlib.client;

import de.dertoaster.multihitboxlib.api.event.client.PartRendererRegistrationEvent;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MHLibModClient implements ClientModInitializer {
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

    @Override
    public void onInitializeClient() {
        EntityRenderEventHandlerCommonLogic.registerRelevantEventListeners();

        // Create the map that will hold the part renderers
        final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map = new HashMap<>();
        PartRendererRegistrationEvent.EVENT.invoker().register(map);

        if (!map.isEmpty()) {
            map.forEach(MHLibModClient::registerEntityPartRenderer);
        }
    }
}
