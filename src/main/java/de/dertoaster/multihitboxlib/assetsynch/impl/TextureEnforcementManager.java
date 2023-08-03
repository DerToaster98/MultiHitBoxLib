package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.IOException;

import de.dertoaster.multihitboxlib.assetsynch.client.TextureClientLogic;
import net.minecraft.resources.ResourceLocation;

public class TextureEnforcementManager extends MHLibEnforcementManager {

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
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
