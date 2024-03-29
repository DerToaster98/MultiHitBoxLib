package de.dertoaster.multihitboxlib.api.event.client;

import java.util.Map;
import java.util.function.Function;

import de.dertoaster.multihitboxlib.api.event.AbstractRegistrationEvent;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.fml.event.IModBusEvent;

public class PartRendererRegistrationEvent extends AbstractRegistrationEvent<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> implements IModBusEvent {

	public PartRendererRegistrationEvent(Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map) {
		super(map);
	}

}
