package de.dertoaster.multihitboxlib.entity.hitbox;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HitboxProfile(
		AssetEnforcementConfig assetConfig,
		boolean syncToModel,
		boolean trustClient,
		int partUpdateSteps,
		int synchedPartUpdateSteps,
		List<String> synchedBones,
		MainHitboxConfig mainHitboxConfig,
		List<SubPartConfig> partConfigs
		) {
	
	public static final Codec<HitboxProfile> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				AssetEnforcementConfig.CODEC.fieldOf("synched-assets").forGetter(HitboxProfile::assetConfig),
				Codec.BOOL.fieldOf("sync-with-model").forGetter(HitboxProfile::syncToModel),
				Codec.BOOL.optionalFieldOf("trust-client", false).forGetter(HitboxProfile::trustClient),
				Codec.INT.optionalFieldOf("part-update-steps", 3).forGetter(HitboxProfile::partUpdateSteps),
				Codec.INT.optionalFieldOf("synched-part-update-steps", 1).forGetter(HitboxProfile::synchedPartUpdateSteps),
				Codec.STRING.listOf().fieldOf("synched-bones").forGetter(HitboxProfile::synchedBones),
				MainHitboxConfig.CODEC.fieldOf("main-hitbox").forGetter(HitboxProfile::mainHitboxConfig),
				SubPartConfig.CODEC.listOf().fieldOf("parts").forGetter(HitboxProfile::partConfigs)
				
			).apply(instance, HitboxProfile::new);
	});

}
