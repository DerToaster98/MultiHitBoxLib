package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.util.LazyLoadField;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Supplier;

public class Constants {
    public static final String MODID = "multihitboxlib";
    public static final String NETWORK_VERSION = "1.0.1";

    public static final LazyLoadField<File> MHLIB_CONFIG_DIR = new LazyLoadField<>(() -> {
        final Path configDir = FabricLoader.getInstance().getConfigDir();
        final File result = new File(configDir.toFile(), "mhlib");
        return result;
    });
    public static final File MHLIB_SYNC_DIR = new File(MHLIB_CONFIG_DIR.get(), "_sync");
    public static final File MHLIB_ASSET_DIR = new File(MHLIB_CONFIG_DIR.get(), "assetsynch");
    public static final String DISABLE_EXAMPLES_PROPERTY_KEY = MODID + ".disable_examples";

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

        public static boolean isModLoaded(final String modid) {
            Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
            for (ModContainer mod : mods) {
                if (mod.getMetadata().getId().equals(modid)) {
                    if(FabricLoader.getInstance().isModLoaded(modid)){ return true; }
                }
            }
            return false;
        }

        public static final Supplier<Boolean> GECKOLIB_LOADED = new ModLoadedPredicate(Dependencies.GECKOLIB_MODID);
        public static final Supplier<Boolean> AZURELIB_LOADED = new ModLoadedPredicate(Dependencies.AZURELIB_MODID);
    }
}