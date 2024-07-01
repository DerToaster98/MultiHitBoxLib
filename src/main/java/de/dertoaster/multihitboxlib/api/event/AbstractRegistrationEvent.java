package de.dertoaster.multihitboxlib.api.event;

import net.neoforged.bus.api.Event;

import java.util.Map;

public class AbstractRegistrationEvent<K, V> extends Event {
	
	private final Map<K, V> REGISTRATION_MAP;
	
	public AbstractRegistrationEvent(Map<K, V> map) {
		this.REGISTRATION_MAP = map;
	}

	public boolean tryAdd(K id, final V value) {
		if (id == null || value == null) {
			return false;
		}
		if (REGISTRATION_MAP.containsKey(id)) {
			return false;
		}
		REGISTRATION_MAP.put(id, value);
		return true;
	}

}
