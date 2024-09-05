package de.dertoaster.multihitboxlib.assetsynch.assetfinders;

import de.dertoaster.multihitboxlib.entity.hitbox.AssetEnforcementConfig;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HitboxProfileAssetFinder extends AbstractAssetFinder {

	private static final Predicate<ResourceLocation> RS_CHECK_PREDICATE = rs -> !rs.toString().isBlank() && !rs.getNamespace().isBlank() && !rs.getPath().isBlank();
	
	@Override
	public Set<ResourceLocation> apply(RegistryAccess registryAccess) {
		Set<ResourceLocation> result = new HashSet<>();
		for (HitboxProfile hp : MHLibDatapackLoaders.HITBOX_PROFILE_REGISTRY.values(registryAccess)) {
			if (hp == null) {
				continue;
			}
			AssetEnforcementConfig aec = hp.assetConfig();
			if (aec == null) {
				continue;
			}
			result.addAll(aec.animations().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
			result.addAll(aec.models().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
			result.addAll(aec.textures().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
		}
		return result;
	}

}
