package de.dertoaster.multihitboxlib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.phys.Vec3;

public record BoneInformation(String name, Vec3 worldPos, Vec3 scale) {

	public static final Vec3 DEFAULT_SCALING = new Vec3(1, 1, 1);

	public static Codec<BoneInformation> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("bone").forGetter(BoneInformation::name), 
				Vec3.CODEC.fieldOf("position").forGetter(BoneInformation::worldPos), 
				Vec3.CODEC.optionalFieldOf("scaling", DEFAULT_SCALING).forGetter(BoneInformation::scale)
			).apply(instance, BoneInformation::new);
	});
}
