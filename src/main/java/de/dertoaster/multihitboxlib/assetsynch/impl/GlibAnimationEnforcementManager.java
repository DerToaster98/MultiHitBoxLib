package de.dertoaster.multihitboxlib.assetsynch.impl;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.object.BakedAnimations;

public class GlibAnimationEnforcementManager extends AbstractAssetEnforcementManager<BakedAnimations> {

	@Override
	protected void registerAsset(ResourceLocation id, BakedAnimations asset) {
		if (GeckoLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib animation with id <" + id.toString() + ">");
		
		GeckoLibCache.getBakedAnimations().put(id, asset);
	}

}
