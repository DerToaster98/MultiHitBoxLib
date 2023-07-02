package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.nio.file.Path;
import java.util.Optional;

import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;

public class TextureEnforcementManager extends AbstractAssetEnforcementManager<AbstractTexture> {

	@Override
	protected void registerAsset(ResourceLocation id, AbstractTexture asset) {
		
	}

	@Override
	protected Optional<byte[]> findAsset(ResourceLocation id) {
		byte[] data = null;

		if (ModList.get() != null && ModList.get().isLoaded(id.getNamespace())) {
			IModFileInfo imfi = ModList.get().getModFileById(id.getNamespace());
			IModFile modFile = imfi.getFile();
			Path resourcePath = modFile.findResource("assets", id.getNamespace(), "textures", id.getPath());
			if (resourcePath == null) {
				return Optional.empty();
			}
			return Optional.ofNullable(encodeFileToBase64(resourcePath));
		}

		return Optional.ofNullable(data);
	}

}
