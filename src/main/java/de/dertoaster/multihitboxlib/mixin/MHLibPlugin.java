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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;

public class MHLibPlugin implements IMixinConfigPlugin {
	
	private static final Supplier<Boolean> TRUE = () -> true;
	
	protected static class ModLoadedPredicate implements Supplier<Boolean> {
		
		private final String MODID;
		
		public ModLoadedPredicate(final String modid) {
			this.MODID = modid;
		}

		@Override
		public Boolean get() {
			ModList ml = ModList.get();
			if (ml == null) {
				// try the loading modlist
				LoadingModList lml = LoadingModList.get();
				if (lml == null) {
					// Odd
					throw new RuntimeException("unable to lookup any modlist!"); 
				} else {
					// Janky, but gets the job done
					return lml.getModFileById(this.MODID) != null;
				}
			}
			return ml.isLoaded(this.MODID);
		}
		
	}
	
	private static final Supplier<Boolean> GECKOLIB_LOADED = new ModLoadedPredicate(Constants.Dependencies.GECKOLIB_MODID);
	private static final Supplier<Boolean> AZURELIB_LOADED = new ModLoadedPredicate(Constants.Dependencies.AZURELIB_MODID);
	
	private static final Map<String, Supplier<Boolean>> CONDITIONS = ImmutableMap.of(
			// Geckolib
			"de.dertoaster.multihitboxlib.mixin.geckolib.MixinGeoEntityRenderer", GECKOLIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.geckolib.MixinGeoReplacedEntityRenderer", GECKOLIB_LOADED,
			// Azurelib
			"de.dertoaster.multihitboxlib.mixin.azurelib.MixinGeoEntityRenderer", AZURELIB_LOADED,
			"de.dertoaster.multihitboxlib.mixin.azurelib.MixinGeoReplacedEntityRenderer", AZURELIB_LOADED
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
