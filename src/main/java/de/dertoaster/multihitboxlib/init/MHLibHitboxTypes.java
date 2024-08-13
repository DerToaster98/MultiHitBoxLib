package de.dertoaster.multihitboxlib.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.entity.hitbox.type.IHitboxType;
import de.dertoaster.multihitboxlib.entity.hitbox.type.implementation.AABBHitboxType;
import net.commoble.databuddy.codec.RegistryDispatcher;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MHLibHitboxTypes {
	
	public static void init() {
		
	}
	
	public static final RegistryDispatcher<IHitboxType> HITBOX_TYPE_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
			MHLibMod.prefix("registry/dispatcher/hitboxtype"),
			rule -> rule.getType(),
			builder -> {}
	); 
	
	public static final DeferredHolder<MapCodec<? extends IHitboxType>> AABB = HITBOX_TYPE_DISPATCHER.defreg().register("aabb", () -> MapCodec.of());
	//public static final RegistryObject<Codec<? extends IHitboxType>> SPHERE = HITBOX_TYPE_DISPATCHER.registry().register("sphere", () -> SphereHitboxType.CODEC);
	//public static final RegistryObject<Codec<? extends IHitboxType>> ORIENTABLE_SPHEROID = HITBOX_TYPE_DISPATCHER.registry().register("orientable_spheroid", () -> OrientableSpheroidHitboxType.CODEC);
	//public static final RegistryObject<Codec<? extends IHitboxType>> OBB = HITBOX_TYPE_DISPATCHER.registry().register("orientable_bb", () -> OrientableBBHitboxType.CODEC);

}
