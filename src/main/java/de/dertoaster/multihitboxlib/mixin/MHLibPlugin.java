package de.dertoaster.multihitboxlib.mixin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableMap;

import de.dertoaster.multihitboxlib.Constants;

public class MHLibPlugin implements IMixinConfigPlugin {
	
	private static final Supplier<Boolean> TRUE = () -> true;
	
	private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
			// Geckolib
			"de.dertoaster.multihitboxlib.mixin.geckolib.MixinGeoEntityRenderer", Constants.Dependencies.GECKOLIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.geckolib.MixinGeoReplacedEntityRenderer", Constants.Dependencies.GECKOLIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.geckolib.MixinGeoRenderer", Constants.Dependencies.GECKOLIB_LOADED,
			// Azurelib
			"de.dertoaster.multihitboxlib.mixin.azurelib.MixinGeoEntityRenderer", Constants.Dependencies.AZURELIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.azurelib.MixinGeoReplacedEntityRenderer", Constants.Dependencies.AZURELIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.azurelib.MixinGeoRenderer", Constants.Dependencies.AZURELIB_LOADED
	);

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return CONDITIONS.getOrDefault(mixinClassName, TRUE).get();
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

}
