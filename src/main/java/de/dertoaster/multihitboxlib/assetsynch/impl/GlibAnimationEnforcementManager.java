package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.nio.file.Path;
import java.util.Optional;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
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
	protected Optional<byte[]> findAsset(ResourceLocation id) {
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
