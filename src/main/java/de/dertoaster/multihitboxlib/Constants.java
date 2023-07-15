package de.dertoaster.multihitboxlib;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.util.LazyLoadField;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

public class Constants {
	
	public static final String MODID = "multihitboxlib";
	public static final String NETWORK_VERSION = "1.0.0";
	
	public static final LazyLoadField<File> MHLIB_CONFIG_DIR = new LazyLoadField<>(() -> {
		final Path configDir = FMLPaths.CONFIGDIR.get();
		final File result = new File(configDir.toFile(), "mhlib");
		return result;
	});
	public static final File MHLIB_SYNC_DIR = new File(MHLIB_CONFIG_DIR.get(), "_sync");
	public static final File MHLIB_ASSET_DIR = new File(MHLIB_CONFIG_DIR.get(), "assetsynch");
	
	public static class Dependencies {
		public static String GECKOLIB_MODID = "geckolib";
		public static String AZURELIB_MODID = "azurelib";
		
		protected static class ModLoadedPredicate implements Supplier<Boolean> {
			
			private final String MODID;
			
			public ModLoadedPredicate(final String modid) {
				this.MODID = modid;
			}

			@Override
			public Boolean get() {
				return isModLoaded(this.MODID);
			}
			
		}
		
		public static final boolean isModLoaded(final String modid) {
			ModList ml = ModList.get();
			if (ml == null) {
				// try the loading modlist
				LoadingModList lml = LoadingModList.get();
				if (lml == null) {
					// Odd
					throw new RuntimeException("unable to lookup any modlist!"); 
				} else {
					// Janky, but gets the job done
					return lml.getModFileById(modid) != null;
				}
			}
			return ml.isLoaded(modid);
		}
		
		public static final Supplier<Boolean> GECKOLIB_LOADED = new ModLoadedPredicate(Constants.Dependencies.GECKOLIB_MODID);
		public static final Supplier<Boolean> AZURELIB_LOADED = new ModLoadedPredicate(Constants.Dependencies.AZURELIB_MODID);
	}

}
