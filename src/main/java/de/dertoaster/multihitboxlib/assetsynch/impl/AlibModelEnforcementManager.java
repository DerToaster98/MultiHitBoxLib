package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.minecraft.resources.ResourceLocation;

public class AlibModelEnforcementManager extends AbstractAssetEnforcementManager<BakedGeoModel> {

	@Override
	protected void registerAsset(ResourceLocation id, BakedGeoModel asset) {
		if (AzureLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib model with id <" + id.toString() + ">");

		AzureLibCache.getBakedModels().put(id, asset);
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
		return "models/" + Constants.Dependencies.AZURELIB_MODID;
	}

	@Override
	public Optional<BakedGeoModel> getAsset(ResourceLocation id) {
		return Optional.ofNullable(AzureLibCache.getBakedModels().get(id));
	}

}
