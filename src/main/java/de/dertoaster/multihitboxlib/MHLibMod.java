package de.dertoaster.multihitboxlib;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MODID)
public class MHLibMod {
	public static final Logger LOGGER = LogUtils.getLogger();

	public MHLibMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Now, initialize all our folders
		initializeConfigDirectories();
		
		MHLibDatapackLoaders.init();
	}

	private static void initializeConfigDirectories() {
		final File mainDir = Constants.MHLIB_CONFIG_DIR.get();
		try {
			checkAndCreateFolder(mainDir);
			checkAndCreateFolder(Constants.MHLIB_ASSET_DIR);
			checkAndCreateFolder(Constants.MHLIB_SYNC_DIR);
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
		MHLibPackets.init();
		
		// Throws registration event and registers all asset enforcers
		AssetEnforcement.init();
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	public static ResourceLocation prefixAssesEnforcementManager(String string) {
		return new ResourceLocation(Constants.MODID, "asset_manager/" + string);
	}

}
