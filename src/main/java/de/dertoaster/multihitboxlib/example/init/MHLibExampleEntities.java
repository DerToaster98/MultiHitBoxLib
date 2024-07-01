package de.dertoaster.multihitboxlib.example.init;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.example.entity.Anjanath;
import de.dertoaster.multihitboxlib.example.entity.AnjanathALib;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MHLibExampleEntities {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MODID);
	
	public static final RegistryObject<EntityType<Anjanath>> ANJANATH = registerSized(Anjanath::new, "anjanath", 6, 5, 1);
	public static final RegistryObject<EntityType<AnjanathALib>> ANJANATH_AL = registerSized(AnjanathALib::new, "anjanath_al", 6, 5, 1);

	protected static <T extends Entity>  RegistryObject<EntityType<T>> registerSized(EntityFactory<T> factory, final String entityName, float width, float height, int updateInterval) {
		RegistryObject<EntityType<T>> result = ENTITY_TYPES.register(entityName, () -> EntityType.Builder
				.<T>of(factory, MobCategory.MISC)
				.sized(width, height)
				.setTrackingRange(128)
				.clientTrackingRange(64)
				.updateInterval(updateInterval)
				.setShouldReceiveVelocityUpdates(true)
				.build(MHLibMod.prefix(entityName).toString()));
		
		return result;
	}
	
	public static void registerEntityTypes()
	{
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

}
