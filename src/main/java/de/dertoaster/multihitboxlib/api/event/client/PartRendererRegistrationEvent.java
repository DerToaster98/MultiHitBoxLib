package de.dertoaster.multihitboxlib.api.event.client;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;

import java.util.Map;
import java.util.function.Function;

public interface PartRendererRegistrationEvent {

    Event<PartRendererRegistrationEvent> EVENT = EventFactory.createArrayBacked(
            PartRendererRegistrationEvent.class,
            (listeners) -> (map) -> {
                for (PartRendererRegistrationEvent listener : listeners) {
                    listener.register(map);
                }
            }
    );

    void register(Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map);
}
