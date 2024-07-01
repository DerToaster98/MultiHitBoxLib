package de.dertoaster.multihitboxlib.init;

import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.DatapackRegistry;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MHLibDatapackLoaders {
	
	public static final DatapackRegistry<HitboxProfile> HITBOX_PROFILE_REGISTRY = new DatapackRegistry<>(MHLibMod.prefix("entity/hitbox_profiles"), HitboxProfile.CODEC);

	@SubscribeEvent
	public static void onRegistryRegistration(DataPackRegistryEvent.NewRegistry event) {
		HITBOX_PROFILE_REGISTRY.registerSynchable(event);
	}

	
	public static Optional<HitboxProfile> getHitboxProfile(ResourceLocation entityID, RegistryAccess registryAccess) {
		return Optional.ofNullable(HITBOX_PROFILE_REGISTRY.get(entityID, registryAccess));
	}

	/*
	 * TODO: Find replacement for getKey
	 */
	public static Optional<HitboxProfile> getHitboxProfile(EntityType<?> entityType, RegistryAccess registryAccess) {
		return getHitboxProfile(Registries.ENTITY_TYPE.getKey(entityType), registryAccess);
	}
	
}
