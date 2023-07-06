package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.nio.file.Path;
import java.util.Optional;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.loading.object.BakedAnimations;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

public class AlibAnimationEnforcementManager extends AbstractAssetEnforcementManager<BakedAnimations> {

	@Override
	protected void registerAsset(ResourceLocation id, BakedAnimations asset) {
		if (AzureLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib animation with id <" + id.toString() + ">");
		
		AzureLibCache.getBakedAnimations().put(id, asset);
	}

	@Override
	protected Optional<byte[]> findAsset(ResourceLocation id) {
		// TODO: Load from config folder instead, this is VERY unreliable
		if (ModList.get() != null && ModList.get().isLoaded(id.getNamespace())) {
			IModFileInfo imfi = ModList.get().getModFileById(id.getNamespace());
			IModFile modFile = imfi.getFile();
			Path resourcePath = modFile.findResource("assets", id.getNamespace(), "animations", id.getPath());
			if (resourcePath == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(encodeFileToBase64(resourcePath));
		}
		
		return Optional.empty();
	}

}
