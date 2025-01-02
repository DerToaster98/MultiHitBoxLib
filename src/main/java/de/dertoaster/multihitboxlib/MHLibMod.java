package de.dertoaster.multihitboxlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import net.minecraft.resources.ResourceLocation;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MODID)
public class MHLibMod {
	public static final Logger LOGGER = LogUtils.getLogger();

	public MHLibMod(IEventBus modEventBus) {

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		// Register ourselves for server and other game events we are interested in
		NeoForge.EVENT_BUS.register(this);

		// Now, initialize all our folders
		initializeConfigDirectories();
		
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

	private void commonSetup(final FMLCommonSetupEvent event) {
		// Throws registration event and registers all asset enforcers
		AssetEnforcement.init();
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	public static ResourceLocation prefixAssesEnforcementManager(String string) {
		return prefix("asset_manager/" + string);
	}

	public static ResourceLocation prefixAssetFinder(String string) {
		return prefix("asset_finder/" + string);
	}

	public static ResourceLocation prefix(String path) {
		return ResourceLocation.fromNamespaceAndPath(Constants.MODID, path.toLowerCase(Locale.ROOT));
	}

	public static boolean shouldRegisterExamples() {
		return !FMLEnvironment.production && !Boolean.getBoolean(Constants.DISABLE_EXAMPLES_PROPERTY_KEY);
	}

}
