package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dertoaster.multihitboxlib.util.UtilityCodecs;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public record SubPartConfig(
		String name,
		boolean collidable,
		boolean canReceiveDamage,
		float damageModifier,
		double maxDeviationFromServer,
		Vec2 baseSize,
		Vec3 baseRotation, //Only to be used for rendering stuff. Won't change the hitbox as that is still axis alinged
		Vec3 basePosition,
		Vec3 pivotOffset
		) {
	
	public static final Codec<SubPartConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("name").forGetter(SubPartConfig::name),
				Codec.BOOL.fieldOf("collidable").forGetter(SubPartConfig::collidable),
				Codec.BOOL.fieldOf("can-receive-damage").forGetter(SubPartConfig::canReceiveDamage),
				Codec.FLOAT.optionalFieldOf("damage-modifier", 1.0F).forGetter(SubPartConfig::damageModifier),
				Codec.DOUBLE.optionalFieldOf("max-deviation-from-server", 0.0D).forGetter(SubPartConfig::maxDeviationFromServer),
				UtilityCodecs.VEC2_CODEC.fieldOf("size").forGetter(SubPartConfig::baseSize),
				Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(SubPartConfig::baseRotation),
				Vec3.CODEC.fieldOf("position").forGetter(SubPartConfig::basePosition),
				Vec3.CODEC.optionalFieldOf("pivot-offset", Vec3.ZERO).forGetter(SubPartConfig::pivotOffset)
			).apply(instance, SubPartConfig::new);
			
	});

}
