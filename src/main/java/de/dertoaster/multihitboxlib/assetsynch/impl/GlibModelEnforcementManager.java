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
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class GlibModelEnforcementManager extends AbstractAssetEnforcementManager<BakedGeoModel> {

	@Override
	protected void registerAsset(ResourceLocation id, BakedGeoModel asset) {
		if (GeckoLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib model with id <" + id.toString() + ">");

		GeckoLibCache.getBakedModels().put(id, asset);
	}

	@Override
	protected Optional<byte[]> findAsset(ResourceLocation id) {
		byte[] data = null;

		if (ModList.get() != null && ModList.get().isLoaded(id.getNamespace())) {
			IModFileInfo imfi = ModList.get().getModFileById(id.getNamespace());
			IModFile modFile = imfi.getFile();
			Path resourcePath = modFile.findResource("assets", id.getNamespace(), "geo", id.getPath());
			if (resourcePath == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(encodeFileToBase64(resourcePath));
		}

		return Optional.ofNullable(data);
	}

}
