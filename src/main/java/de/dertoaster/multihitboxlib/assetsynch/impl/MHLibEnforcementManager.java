package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;

public abstract class MHLibEnforcementManager extends AbstractAssetEnforcementManager {

	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		File location = this.getFileForId(id);
		if (!location.exists() || !location.isFile()) {
			return Optional.empty();
		}
		return Optional.ofNullable(encodeToBytes(location.toPath()));
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
	
}
