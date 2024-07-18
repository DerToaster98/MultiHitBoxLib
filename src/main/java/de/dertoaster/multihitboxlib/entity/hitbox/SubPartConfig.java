package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.dertoaster.multihitboxlib.entity.hitbox.type.IHitboxType;
import de.dertoaster.multihitboxlib.init.MHLibHitboxTypes;

public record SubPartConfig(
		String name,
		boolean collidable,
		boolean canReceiveDamage,
		float damageModifier,
		double maxDeviationFromServer,
		IHitboxType hitboxType
		) {
	
	public static final Codec<SubPartConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("name").forGetter(SubPartConfig::name),
				Codec.BOOL.fieldOf("collidable").forGetter(SubPartConfig::collidable),
				Codec.BOOL.fieldOf("can-receive-damage").forGetter(SubPartConfig::canReceiveDamage),
				Codec.FLOAT.optionalFieldOf("damage-modifier", 1.0F).forGetter(SubPartConfig::damageModifier),
				Codec.DOUBLE.optionalFieldOf("max-deviation-from-server", 0.0D).forGetter(SubPartConfig::maxDeviationFromServer),
				MHLibHitboxTypes.HITBOX_TYPE_DISPATCHER.dispatchedCodec().fieldOf("box").forGetter(SubPartConfig::hitboxType)
			).apply(instance, SubPartConfig::new);
			
	});

}
