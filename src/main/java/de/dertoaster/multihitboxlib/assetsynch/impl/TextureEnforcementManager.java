package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.assetsynch.AbstractNInOneEntriesEnforcementManager;
import de.dertoaster.multihitboxlib.assetsynch.client.TextureClientLogic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;

public class TextureEnforcementManager extends AbstractNInOneEntriesEnforcementManager {

	@Override
	protected List<byte[]> getRawByteEntriesFor(ResourceLocation id) {
		List<byte[]> result = new ArrayList<>(2);
		File location = this.getFileForId(id);
		if (!location.exists() || !location.isFile()) {
			return List.of();
		}
		try {
			result.add(Files.readAllBytes(location.toPath()));
		} catch (IOException e1) {
			e1.printStackTrace();
			return List.of();
		}
		File metaFile = this.getFileForId(id.withSuffix(".mcmeta"));
		if (metaFile.exists() && metaFile.isFile()) {
			byte[] metaBytes = null;
			try {
				metaBytes = Files.readAllBytes(metaFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				metaBytes = new byte[] {};
			}
			if (metaBytes != null && metaBytes.length > 0) {
				result.add(metaBytes);
			}
		}

		return result;
	}

	@Override
	protected boolean loadEntry(ResourceLocation id, byte[] data, int index) {
		if (index != 0) {
			return true;
		}
		if (data == null) {
			return false;
		}
		if (data.length <= 0) {
			return false;
		}
		// Here goes nothing...
		try {
			return TextureClientLogic.receiveAndLoad(this, id, data);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected boolean writeEntryToFile(ResourceLocation id, byte[] data, int index) {
		ResourceLocation idToUse = id;
		if (index != 0) {
			idToUse = id.withSuffix(".mcmeta");
		}
		File target = this.getFileForId(idToUse);
		return ensureFileFor(target, idToUse) && writeToFile(target, data);
	}

	@Nonnull
	@Override
	protected File createServerDirectory() {
		return new File(Constants.MHLIB_ASSET_DIR, this.getSubDirectoryName());
	}

	@Nonnull
	@Override
	protected File createSynchDirectory() {
		return new File(Constants.MHLIB_SYNC_DIR, this.getSubDirectoryName());
	}

	/*
	 * TODO: Return proper location
	 */
	@Override
	public PackLocationInfo location() {
		return null;
	}
}
