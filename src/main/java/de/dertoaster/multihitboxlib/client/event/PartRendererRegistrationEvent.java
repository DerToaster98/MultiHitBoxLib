package de.dertoaster.multihitboxlib.client.event;

import java.util.Map;
import java.util.function.Function;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraftforge.eventbus.api.Event;

public class PartRendererRegistrationEvent extends Event {

	private final Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> REGISTRATION_MAP;
	
	public PartRendererRegistrationEvent(Map<Class<? extends MHLibPartEntity<?>>, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>>> map) {
		this.REGISTRATION_MAP = map;
	}

	public boolean tryAdd(Class<? extends MHLibPartEntity<?>> partClass, Function<EntityRenderDispatcher, ? extends EntityRenderer<? extends MHLibPartEntity<?>>> rendererProvider) {
		if (partClass == null || rendererProvider == null) {
			return false;
		}
		if (REGISTRATION_MAP.containsKey(partClass)) {
			return false;
		}
		REGISTRATION_MAP.put(partClass, rendererProvider);
		return true;
	}
	
	@Override
	public boolean isCancelable() {
		return false;
	}

}
