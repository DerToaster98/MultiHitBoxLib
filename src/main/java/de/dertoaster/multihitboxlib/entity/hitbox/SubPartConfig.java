package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.phys.Vec3;

public record SubPartConfig(
		String name,
		boolean collidable,
		boolean canReceiveDamage,
		double damageModifier,
		Vec3 baseSize,
		Vec3 baseRotation,
		Vec3 basePosition,
		Vec3 pivotOffset
		) {
	
	public static final Codec<SubPartConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("name").forGetter(SubPartConfig::name),
				Codec.BOOL.fieldOf("collidable").forGetter(SubPartConfig::collidable),
				Codec.BOOL.fieldOf("canReceiveDamage").forGetter(SubPartConfig::canReceiveDamage),
				Codec.DOUBLE.optionalFieldOf("damageModifier", 1.0D).forGetter(SubPartConfig::damageModifier),
				Vec3.CODEC.fieldOf("size").forGetter(SubPartConfig::baseSize),
				Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(SubPartConfig::baseRotation),
				Vec3.CODEC.fieldOf("position").forGetter(SubPartConfig::basePosition),
				Vec3.CODEC.optionalFieldOf("pivotOffset", Vec3.ZERO).forGetter(SubPartConfig::pivotOffset)
			).apply(instance, SubPartConfig::new);
			
	});

}
