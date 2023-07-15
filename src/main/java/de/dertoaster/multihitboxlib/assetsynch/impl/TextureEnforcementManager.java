package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;

public class TextureEnforcementManager extends AbstractAssetEnforcementManager<Object> {

	@Override
	protected void registerAsset(ResourceLocation id, Object asset) {
		
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
		return "textures";
	}

	@Override
	public Optional<Object> getAsset(ResourceLocation id) {
		return Optional.empty();
	}

}
