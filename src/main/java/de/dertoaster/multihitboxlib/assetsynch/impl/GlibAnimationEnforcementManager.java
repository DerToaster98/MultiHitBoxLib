package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.util.Optional;

import com.google.gson.JsonObject;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;

public class GlibAnimationEnforcementManager extends MHLibEnforcementManager {

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		if (GeckoLibCache.getBakedAnimations().containsKey(id))
			MHLibMod.LOGGER.debug("Overriding geckolib animation with id <" + id.toString() + ">");
		
		Optional<BakedAnimations> optAnimations = this.createBakedAnimations(data);
		
		optAnimations.ifPresent(asset -> GeckoLibCache.getBakedAnimations().put(id, asset));
		return optAnimations.isPresent();
	}

	/*
	 * DONE: Find replacement for GEO_GSON => Location changed to KeyFramesAdapter
	 */
	private Optional<BakedAnimations> createBakedAnimations(byte[] data) {
		String stringData = new String(data);
		JsonObject jo = GsonHelper.fromJson(KeyFramesAdapter.GEO_GSON, stringData, JsonObject.class);
		if (jo != null) {
			return Optional.of(KeyFramesAdapter.GEO_GSON.fromJson(jo, BakedAnimations.class));
		}
		return Optional.empty();
	}

	@Override
	public String getSubDirectoryName() {
		return "animations/" + Constants.Dependencies.GECKOLIB_MODID;
	}

	/*
	 * TODO: Return proper location
	 */
	@Override
	public PackLocationInfo location() {
		return null;
	}

}
