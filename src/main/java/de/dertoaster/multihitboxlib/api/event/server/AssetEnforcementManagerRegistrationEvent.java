package de.dertoaster.multihitboxlib.api.event.server;

import java.util.Map;

import de.dertoaster.multihitboxlib.api.event.AbstractRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.IModBusEvent;

public class AssetEnforcementManagerRegistrationEvent extends AbstractRegistrationEvent<ResourceLocation, AbstractAssetEnforcementManager> implements IModBusEvent {

	public AssetEnforcementManagerRegistrationEvent(Map<ResourceLocation, AbstractAssetEnforcementManager> map) {
		super(map);
	}
	
}
