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
	
	public static final DeferredHolder<MapCodec<? extends IHitboxType>, MapCodec<AABBHitboxType>> AABB = HITBOX_TYPE_DISPATCHER.defreg().register("aabb", () -> AABBHitboxType.CODEC);
	//public static final DeferredHolder<MapCodec<? extends IHitboxType>, MapCodec<SphereHitboxType>> SPHERE = HITBOX_TYPE_DISPATCHER.registry().register("sphere", () -> SphereHitboxType.CODEC);
	//public static final DeferredHolder<MapCodec<? extends IHitboxType>, MapCodec<OrientableSpheroidHitboxType>> ORIENTABLE_SPHEROID = HITBOX_TYPE_DISPATCHER.registry().register("orientable_spheroid", () -> OrientableSpheroidHitboxType.CODEC);
	//public static final DeferredHolder<MapCodec<? extends IHitboxType>, MapCodec<OrientableBBHitboxType>> OBB = HITBOX_TYPE_DISPATCHER.registry().register("orientable_bb", () -> OrientableBBHitboxType.CODEC);

}
