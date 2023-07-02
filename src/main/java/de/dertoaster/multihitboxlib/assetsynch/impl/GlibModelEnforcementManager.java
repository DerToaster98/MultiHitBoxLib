package de.dertoaster.multihitboxlib.assetsynch.impl;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GlibModelEnforcementManager extends AbstractAssetEnforcementManager<BakedGeoModel> {
	
	@Override
	protected void registerAsset(ResourceLocation id, BakedGeoModel asset) {
		if (GeckoLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib model with id <" + id.toString() + ">");
		
		GeckoLibCache.getBakedModels().put(id, asset);
	}

}
