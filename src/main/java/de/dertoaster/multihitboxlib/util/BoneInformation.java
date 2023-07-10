package de.dertoaster.multihitboxlib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.phys.Vec3;

public record BoneInformation(String name, boolean hidden, Vec3 worldPos, Vec3 scale, Vec3 rotation) {

	public static final Vec3 DEFAULT_SCALING = new Vec3(1, 1, 1);

	public static Codec<BoneInformation> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				Codec.STRING.fieldOf("bone").forGetter(BoneInformation::name), 
				Codec.BOOL.fieldOf("hidden").forGetter(BoneInformation::hidden),
				Vec3.CODEC.fieldOf("position").forGetter(BoneInformation::worldPos), 
				Vec3.CODEC.optionalFieldOf("scaling", DEFAULT_SCALING).forGetter(BoneInformation::scale),
				Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(BoneInformation::rotation)
			).apply(instance, BoneInformation::new);
	});
}
