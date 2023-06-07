package de.dertoaster.multihitboxlib.entity.hitbox;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public record HitboxProfile(
		AssetEnforcementConfig assetConfig,
		List<String> synchedBones,
		SubPartConfig mainHitboxConfig,
		List<SubPartConfig> partConfigs
		) {
	
	public static final Codec<HitboxProfile> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				AssetEnforcementConfig.CODEC.fieldOf("synched-assets").forGetter(HitboxProfile::assetConfig),
				Codec.STRING.listOf().fieldOf("synched-bones").forGetter(HitboxProfile::synchedBones),
				SubPartConfig.CODEC.fieldOf("main-hitbox").forGetter(HitboxProfile::mainHitboxConfig),
				SubPartConfig.CODEC.listOf().fieldOf("parts").forGetter(HitboxProfile::partConfigs)
				
			).apply(instance, HitboxProfile::new);
	});

}
