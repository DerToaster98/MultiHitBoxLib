package de.dertoaster.multihitboxlib.init;

import java.util.Optional;

import commoble.databuddy.data.CodecJsonDataManager;
import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.network.server.datapacksync.SPacketSyncHitboxProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MHLibDatapackLoaders {

	public static final CodecJsonDataManager<HitboxProfile> HITBOX_PROFILES = new CodecJsonDataManager<>("entity/hitbox_profiles", HitboxProfile.CODEC);
	
	public static void init() {
		HITBOX_PROFILES.subscribeAsSyncable(MHLibPackets.MHLIB_NETWORK, SPacketSyncHitboxProfile::new);
	}
	
	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(HITBOX_PROFILES);
	}
	
	public static Optional<HitboxProfile> getHitboxProfile(ResourceLocation entityID) {
		return Optional.ofNullable(HITBOX_PROFILES.getData().getOrDefault(entityID, null));
	}
	
	public static Optional<HitboxProfile> getHitboxProfile(EntityType<?> entityType) {
		return getHitboxProfile(ForgeRegistries.ENTITY_TYPES.getKey(entityType));
	}
	
}
