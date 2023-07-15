package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.util.Optional;

import com.google.gson.JsonObject;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.loading.object.BakedAnimations;
import mod.azure.azurelib.util.JsonUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class AlibAnimationEnforcementManager extends AbstractAssetEnforcementManager {

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
		if (AzureLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding azurelib animation with id <" + id.toString() + ">");
		
		Optional<BakedAnimations> optAnimations = this.createBakedAnimations(data);
		
		optAnimations.ifPresent(asset -> AzureLibCache.getBakedAnimations().put(id, asset));
		return optAnimations.isPresent();
	}

	private Optional<BakedAnimations> createBakedAnimations(byte[] data) {
		String stringData = new String(data);
		JsonObject jo = GsonHelper.fromJson(JsonUtil.GEO_GSON, stringData, JsonObject.class);
		if (jo != null) {
			return Optional.of(JsonUtil.GEO_GSON.fromJson(jo, BakedAnimations.class));
		}
		return Optional.empty();
	}

	@Override
	public String getSubDirectoryName() {
		return "animations/" + Constants.Dependencies.AZURELIB_MODID;
	}

}
