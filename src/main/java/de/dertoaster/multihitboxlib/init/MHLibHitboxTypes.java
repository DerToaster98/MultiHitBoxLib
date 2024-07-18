package de.dertoaster.multihitboxlib.init;

import com.mojang.serialization.Codec;

import commoble.databuddy.codec.RegistryDispatcher;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.entity.hitbox.type.IHitboxType;
import de.dertoaster.multihitboxlib.entity.hitbox.type.implementation.AABBHitboxType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

public class MHLibHitboxTypes {
	
	public static void init() {
		
	}
	
	public static final RegistryDispatcher<IHitboxType> HITBOX_TYPE_DISPATCHER = RegistryDispatcher.makeDispatchForgeRegistry(
			FMLJavaModLoadingContext.get().getModEventBus(), 
			MHLibMod.prefix("registry/dispatcher/hitboxtype"), 
			rule -> rule.getType(),
			builder -> {}
	); 
	
	public static final RegistryObject<Codec<? extends IHitboxType>> AABB = HITBOX_TYPE_DISPATCHER.registry().register("aabb", () -> AABBHitboxType.CODEC);

}
