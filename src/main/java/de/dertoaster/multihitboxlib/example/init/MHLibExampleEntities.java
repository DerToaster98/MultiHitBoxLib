package de.dertoaster.multihitboxlib.example.init;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.example.entity.Anjanath;
import de.dertoaster.multihitboxlib.example.entity.AnjanathALib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MHLibExampleEntities {
	
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Constants.MODID);
	
	public static final Supplier<EntityType<Anjanath>> ANJANATH = registerSized(Anjanath::new, "anjanath", 6, 5, 1);
	public static final Supplier<EntityType<AnjanathALib>> ANJANATH_AL = registerSized(AnjanathALib::new, "anjanath_al", 6, 5, 1);

	protected static <T extends Entity>  Supplier<EntityType<T>> registerSized(EntityFactory<T> factory, final String entityName, float width, float height, int updateInterval) {
		Supplier<EntityType<T>> result = ENTITY_TYPES.register(entityName, () -> EntityType.Builder
				.<T>of(factory, MobCategory.MISC)
				.sized(width, height)
				.setTrackingRange(128)
				.clientTrackingRange(64)
				.updateInterval(updateInterval)
				.setShouldReceiveVelocityUpdates(true)
				.build(MHLibMod.prefix(entityName).toString()));
		
		return result;
	}

	/*
	 * TODO: Find replacement for FMLJavaModLoadingContext
	 */
	public static void registerEntityTypes()
	{
		ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

}
