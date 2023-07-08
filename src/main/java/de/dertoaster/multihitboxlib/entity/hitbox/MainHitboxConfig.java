package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.dertoaster.multihitboxlib.util.UtilityCodecs;
import net.minecraft.world.phys.Vec2;

public record MainHitboxConfig(
		boolean collidable,
		boolean canReceiveDamage,
		double damageModifier,
		Vec2 baseSize
		) {
	
	public static final Codec<MainHitboxConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.BOOL.fieldOf("collidable").forGetter(MainHitboxConfig::collidable),
				Codec.BOOL.fieldOf("canReceiveDamage").forGetter(MainHitboxConfig::canReceiveDamage),
				Codec.DOUBLE.optionalFieldOf("damageModifier", 1.0D).forGetter(MainHitboxConfig::damageModifier),
				UtilityCodecs.VEC2_CODEC.optionalFieldOf("size", Vec2.ZERO).forGetter(MainHitboxConfig::baseSize)
			).apply(instance, MainHitboxConfig::new);
			
	});
	

}
