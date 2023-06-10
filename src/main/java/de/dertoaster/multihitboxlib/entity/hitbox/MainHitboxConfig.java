package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.phys.Vec3;

public record MainHitboxConfig(
		boolean collidable,
		boolean canReceiveDamage,
		double damageModifier,
		Vec3 baseSize
		) {
	
	public static final Codec<MainHitboxConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.BOOL.fieldOf("collidable").forGetter(MainHitboxConfig::collidable),
				Codec.BOOL.fieldOf("canReceiveDamage").forGetter(MainHitboxConfig::canReceiveDamage),
				Codec.DOUBLE.optionalFieldOf("damageModifier", 1.0D).forGetter(MainHitboxConfig::damageModifier),
				Vec3.CODEC.fieldOf("size").forGetter(MainHitboxConfig::baseSize)
			).apply(instance, MainHitboxConfig::new);
			
	});
	

}
