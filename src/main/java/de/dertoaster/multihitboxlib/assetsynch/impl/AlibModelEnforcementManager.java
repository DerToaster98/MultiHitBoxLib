package de.dertoaster.multihitboxlib.assetsynch.impl;

import com.google.gson.JsonObject;
import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.loading.json.FormatVersion;
import mod.azure.azurelib.loading.json.raw.Model;
import mod.azure.azurelib.loading.object.BakedModelFactory;
import mod.azure.azurelib.loading.object.GeometryTree;
import mod.azure.azurelib.util.JsonUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.Optional;

public class AlibModelEnforcementManager extends MHLibEnforcementManager {

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		if (AzureLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding azurelib model with id <" + id.toString() + ">");
		
		Optional<Model> optModel = this.createBakedModel(data);
		
		optModel.ifPresent(rawModel -> {
			if (rawModel.formatVersion() != FormatVersion.V_1_12_0) {
				MHLibMod.LOGGER.warn("Unsupported geometry json version. Supported versions: 1.12.0, id: {}", id);
			} else {
				BakedGeoModel baked = BakedModelFactory.getForNamespace(id.getNamespace()).constructGeoModel(GeometryTree.fromModel(rawModel));
				if (baked != null) {
					AzureLibCache.getBakedModels().put(id, baked);
				}
			}
		});
		return optModel.isPresent() && optModel.get().formatVersion() == FormatVersion.V_1_12_0;
	}

	private Optional<Model> createBakedModel(byte[] data) {
		String stringData = new String(data);
		JsonObject jo = GsonHelper.fromJson(JsonUtil.GEO_GSON, stringData, JsonObject.class);
		if (jo != null) {
			return Optional.of(JsonUtil.GEO_GSON.fromJson(jo, Model.class));
		}
		return Optional.empty();
	}

	@Override
	public String getSubDirectoryName() {
		return "models/" + Constants.Dependencies.AZURELIB_MODID;
	}

}
