package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.util.Optional;

import com.google.gson.JsonObject;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.util.JsonUtil;

public class GlibAnimationEnforcementManager extends MHLibEnforcementManager {

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		if (GeckoLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib animation with id <" + id.toString() + ">");
		
		Optional<BakedAnimations> optAnimations = this.createBakedAnimations(data);
		
		optAnimations.ifPresent(asset -> GeckoLibCache.getBakedAnimations().put(id, asset));
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
		return "animations/" + Constants.Dependencies.GECKOLIB_MODID;
	}

}
