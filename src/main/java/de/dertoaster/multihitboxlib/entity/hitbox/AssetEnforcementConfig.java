package de.dertoaster.multihitboxlib.entity.hitbox;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record AssetEnforcementConfig(
		List<ResourceLocation> models,
		List<ResourceLocation> animations,
		List<ResourceLocation> textures
		) {
	
	public static final Codec<AssetEnforcementConfig> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				ResourceLocation.CODEC.listOf().fieldOf("models").forGetter(AssetEnforcementConfig::models),
				ResourceLocation.CODEC.listOf().fieldOf("animations").forGetter(AssetEnforcementConfig::animations),
				ResourceLocation.CODEC.listOf().fieldOf("textures").forGetter(AssetEnforcementConfig::textures)
			).apply(instance, AssetEnforcementConfig::new);
	});

}
