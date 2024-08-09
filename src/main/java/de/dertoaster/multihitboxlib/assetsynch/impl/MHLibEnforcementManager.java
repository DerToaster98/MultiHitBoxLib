package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

public abstract class MHLibEnforcementManager extends AbstractAssetEnforcementManager {

	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		// Looks in the config directories first!
		File location = this.getFileForId(id);
		if (!location.exists() || !location.isFile()) {
			// We couldn't find anything in the config, let's check the mod jar...
			// TODO: Implement, but this is hacky, so watch out! Also if you found it, write the file to disk
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
