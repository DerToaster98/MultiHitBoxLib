package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.example.TestingEntity;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class MHLibMod implements ModInitializer {
	public static final String MODID = "multihitboxlib";
	public static EntityType<TestingEntity> DOOMHUNTER;
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
		DOOMHUNTER = mob("doom_hunter", TestingEntity::new, 3.0f, 7.0F);
		FabricDefaultAttributeRegistry.register(DOOMHUNTER, Mob.createMobAttributes());
	}

	private static <T extends Entity> EntityType<T> mob(String id, EntityType.EntityFactory<T> factory, float height, float width) {
		final var type = FabricEntityTypeBuilder.create(MobCategory.MONSTER, factory).dimensions(
				EntityDimensions.scalable(height, width)).fireImmune().trackedUpdateRate(1).trackRangeBlocks(
				90).build();
		Registry.register(BuiltInRegistries.ENTITY_TYPE, MHLibMod.id(id), type);

		return type;
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