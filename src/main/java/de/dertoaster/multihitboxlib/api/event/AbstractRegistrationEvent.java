package de.dertoaster.multihitboxlib.api.event;

import java.util.Map;

public interface AbstractRegistrationEvent<K, V> {
	static <K, V> boolean tryAdd(Map<K, V> registrationMap, K id, V value) {
		if (id == null || value == null) {
			return false;
		}
		if (registrationMap.containsKey(id)) {
			return false;
		}
		registrationMap.put(id, value);
		return true;
	}
}


