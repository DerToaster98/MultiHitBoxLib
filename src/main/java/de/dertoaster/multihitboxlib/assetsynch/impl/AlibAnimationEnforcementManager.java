package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;

public class AlibAnimationEnforcementManager extends AbstractAssetEnforcementManager {

	/*@Override
	protected void registerAsset(ResourceLocation id, BakedAnimations asset) {
		if (AzureLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib animation with id <" + id.toString() + ">");
		
		AzureLibCache.getBakedAnimations().put(id, asset);
	}*/

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
		return "animations/" + Constants.Dependencies.AZURELIB_MODID;
	}

}
