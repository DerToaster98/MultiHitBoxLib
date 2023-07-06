package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.nio.file.Path;
import java.util.Optional;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

public class AlibModelEnforcementManager extends AbstractAssetEnforcementManager<BakedGeoModel> {

	@Override
	protected void registerAsset(ResourceLocation id, BakedGeoModel asset) {
		if (AzureLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib model with id <" + id.toString() + ">");

		AzureLibCache.getBakedModels().put(id, asset);
	}

	@Override
	protected Optional<byte[]> findAsset(ResourceLocation id) {
		// TODO: Load from config folder instead, this is VERY unreliable
		if (ModList.get() != null && ModList.get().isLoaded(id.getNamespace())) {
			IModFileInfo imfi = ModList.get().getModFileById(id.getNamespace());
			IModFile modFile = imfi.getFile();
			Path resourcePath = modFile.findResource("assets", id.getNamespace(), "geo", id.getPath());
			if (resourcePath == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(encodeFileToBase64(resourcePath));
		}

		return Optional.empty();
	}

}
