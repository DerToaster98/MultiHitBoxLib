package de.dertoaster.multihitboxlib.assetsynch.assetfinders;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Function;

public abstract class AbstractAssetFinder implements Function<RegistryAccess, Set<ResourceLocation>> {

	private ResourceLocation id;
	
	public ResourceLocation getId() {
		return this.id;
	}
	
	void setId(final ResourceLocation id) {
		if (this.id == null) {
			this.id = id;
		}
	}
	
	public Set<ResourceLocation> get(RegistryAccess registryAccess) {
		return apply(registryAccess);
	}
	
}
