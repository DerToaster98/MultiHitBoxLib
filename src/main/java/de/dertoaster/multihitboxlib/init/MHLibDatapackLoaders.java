package de.dertoaster.multihitboxlib.init;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.DatapackRegistry;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent.NewRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MHLibDatapackLoaders {
	
	public static final DatapackRegistry<HitboxProfile> HITBOX_PROFILE_REGISTRY = new DatapackRegistry<>(MHLibMod.prefix("hitbox_profiles"), HitboxProfile.CODEC);

	@SubscribeEvent
	public static void onRegistryRegistration(NewRegistry event) {
		HITBOX_PROFILE_REGISTRY.registerSynchable(event);
	}

	public static Optional<HitboxProfile> getHitboxProfile(ResourceLocation entityID, RegistryAccess registryAccess) {
		return Optional.ofNullable(HITBOX_PROFILE_REGISTRY.get(entityID, registryAccess));
	}
	
	public static Optional<HitboxProfile> getHitboxProfile(EntityType<?> entityType, RegistryAccess registryAccess) {
		return getHitboxProfile(ForgeRegistries.ENTITY_TYPES.getKey(entityType), registryAccess);
	}
	
}
