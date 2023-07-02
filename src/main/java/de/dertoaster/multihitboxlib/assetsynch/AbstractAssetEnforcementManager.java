package de.dertoaster.multihitboxlib.assetsynch;

import java.util.Map;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public abstract class AbstractAssetEnforcementManager<T extends Object> {
	
	private static final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> REGISTERED_MANAGERS = new Object2ObjectArrayMap<>();
	
	public static void init() {
		final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> map = new Object2ObjectArrayMap<>();
		AssetEnforcementManagerRegistrationEvent event = new AssetEnforcementManagerRegistrationEvent(map);
		MinecraftForge.EVENT_BUS.post(event);
		if (map != null) {
			map.entrySet().forEach(entry -> registerEnforcementManager(entry.getKey(), entry.getValue()));
		}
	}

	protected static void registerEnforcementManager(ResourceLocation key, AbstractAssetEnforcementManager<?> value) {
		if (key == null) {
			MHLibMod.LOGGER.error("Can not register asset enforcer with null key!");
			return;
		}
		try {
			REGISTERED_MANAGERS.put(key, value);
		} catch (NullPointerException npe) {
			MHLibMod.LOGGER.error("Asset enforcement manager for id <" + key.toString() + "> could NOT be registered!");
		}
	}
	
	protected abstract void registerAsset(ResourceLocation id, T asset);

}
