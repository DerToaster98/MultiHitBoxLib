package de.dertoaster.multihitboxlib.api.event.client;

import de.dertoaster.multihitboxlib.api.event.AbstractRegistrationEvent;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.neoforged.fml.event.IModBusEvent;

import java.util.Map;
import java.util.function.Function;

public class PartRendererRegistrationEvent extends AbstractRegistrationEvent<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> implements IModBusEvent {

	public PartRendererRegistrationEvent(Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map) {
		super(map);
	}

}
