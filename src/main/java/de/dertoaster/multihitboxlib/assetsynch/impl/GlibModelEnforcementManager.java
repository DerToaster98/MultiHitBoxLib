package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.util.Optional;

import com.google.gson.JsonObject;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.FormatVersion;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.JsonUtil;

public class GlibModelEnforcementManager extends MHLibEnforcementManager {

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		if (GeckoLibCache.getBakedModels().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib model with id <" + id.toString() + ">");
		
		Optional<Model> optModel = this.createBakedModel(data);
		
		optModel.ifPresent(rawModel -> {
			if (rawModel.formatVersion() != FormatVersion.V_1_12_0) {
				MHLibMod.LOGGER.warn("Unsupported geometry json version. Supported versions: 1.12.0, id: {}", id);
			} else {
				BakedGeoModel baked = BakedModelFactory.getForNamespace(id.getNamespace()).constructGeoModel(GeometryTree.fromModel(rawModel));
				if (baked != null) {
					GeckoLibCache.getBakedModels().put(id, baked);
				}
			}
		});
		return optModel.isPresent() && optModel.get().formatVersion() == FormatVersion.V_1_12_0;
	}

	/*
	 * TODO: Find replacement for GEO_GSON
	 */
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
		return "models/" + Constants.Dependencies.GECKOLIB_MODID;
	}

	/*
	 * TODO: Return proper location
	 */
	@Override
	public PackLocationInfo location() {
		return null;
	}

}
