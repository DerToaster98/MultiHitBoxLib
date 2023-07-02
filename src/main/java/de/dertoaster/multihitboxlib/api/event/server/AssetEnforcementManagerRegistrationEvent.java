package de.dertoaster.multihitboxlib.api.event.server;

import java.util.Map;

import de.dertoaster.multihitboxlib.api.event.AbstractRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;

public class AssetEnforcementManagerRegistrationEvent extends AbstractRegistrationEvent<ResourceLocation, AbstractAssetEnforcementManager<?>> {

	public AssetEnforcementManagerRegistrationEvent(Map<ResourceLocation, AbstractAssetEnforcementManager<?>> map) {
		super(map);
	}
	
}
