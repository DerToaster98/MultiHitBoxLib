package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MHLibMod implements ModInitializer {
	public static final String MODID = "multihitboxlib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {

		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			MHLibPackets.registerReceiveServerToClient();
		}

		if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
			MHLibPackets.registerReceiveClientToServer();
		}

		MHLibDatapackLoaders.init();
	}



	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}

	public static ResourceLocation prefix(String path) {
		return new ResourceLocation(Constants.MODID, path.toLowerCase(Locale.ROOT));
	}

	public static void checkAndCreateFolder(File directory) throws IOException {
		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				throw new IOException("Unable to create directory <" + directory.getAbsolutePath() + "!");
			}
		} else if (!directory.isDirectory()) {
			if (directory.delete()) {
				checkAndCreateFolder(directory);
			} else {
				throw new IOException("Directory <" + directory.getAbsolutePath() + "> is a file and could not be deleted!");
			}
		}
	}
}