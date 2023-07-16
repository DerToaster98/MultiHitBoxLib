package de.dertoaster.multihitboxlib.api.event.server;

import java.util.Map;

import de.dertoaster.multihitboxlib.api.event.AbstractRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.assetfinders.AbstractAssetFinder;
import net.minecraft.resources.ResourceLocation;

public class SynchAssetFinderRegistrationEvent extends AbstractRegistrationEvent<ResourceLocation, AbstractAssetFinder> {

	public SynchAssetFinderRegistrationEvent(Map<ResourceLocation, AbstractAssetFinder> map) {
		super(map);
	}

}
