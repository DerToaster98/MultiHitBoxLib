package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
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

	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		File location = new File(this.getSidedDirectory(), id.getNamespace() + "/" + id.getPath());
		if (!location.exists() || !location.isFile()) {
			return Optional.empty();
		}
		return Optional.ofNullable(encodeFileToBase64(location.toPath()));
	}

	@Override
	protected boolean receiveAndLoad(ResourceLocation id, byte[] data) {
		return true;
	}

	@Override
	public String getSubDirectoryName() {
		return "animations/" + Constants.Dependencies.GECKOLIB_MODID;
	}

	@Override
	public Optional<BakedAnimations> getAsset(ResourceLocation id) {
		return Optional.ofNullable(GeckoLibCache.getBakedAnimations().get(id));
	}

}
