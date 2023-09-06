package de.dertoaster.multihitboxlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.GeckoLibMod;

public class MHLibMod implements ModInitializer {
	
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		MHLibPackets.init();
		
		// Throws registration event and registers all asset enforcers
		AssetEnforcement.init();
		// Now, initialize all our folders
		
		initializeConfigDirectories();
		
		MHLibDatapackLoaders.init();
		
		if (shouldRegisterExamples()) {
			//MHLibExampleEntities.registerEntityTypes();
		}
	}

	private static void initializeConfigDirectories() {
		final File mainDir = Constants.MHLIB_CONFIG_DIR.get();
		try {
			checkAndCreateFolder(mainDir);
			checkAndCreateFolder(Constants.MHLIB_ASSET_DIR);
			checkAndCreateFolder(Constants.MHLIB_SYNC_DIR);
			
			// Hide the synch dir
			Files.setAttribute(Constants.MHLIB_SYNC_DIR.toPath(), "dos:hidden", true);
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Log
		}
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

	public static ResourceLocation prefixAssesEnforcementManager(String string) {
		return prefix("asset_manager/" + string);
	}

	public static ResourceLocation prefixAssetFinder(String string) {
		return prefix("asset_finder/" + string);
	}

	public static ResourceLocation prefix(String path) {
		return new ResourceLocation(Constants.MODID, path);
	}
	
	/**
	 * By default, GeckoLib will register and activate several example entities,
	 * items, and blocks when in dev.<br>
	 * These examples are <u>not</u> present when in a production environment
	 * (normal players).<br>
	 * This can be disabled by setting the
	 * {@link GeckoLibMod#DISABLE_EXAMPLES_PROPERTY_KEY} to false in your run args
	 */
	public static boolean shouldRegisterExamples() {
		return false;
	}

}
