package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import de.dertoaster.multihitboxlib.assetsynch.client.TextureClientLogic;
import net.minecraft.resources.ResourceLocation;

public class TextureEnforcementManager extends AbstractAssetEnforcementManager {

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
		// Here goes nothing...
		try {
			return TextureClientLogic.receiveAndLoad(this, id, data);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String getSubDirectoryName() {
		return "textures";
	}

}
