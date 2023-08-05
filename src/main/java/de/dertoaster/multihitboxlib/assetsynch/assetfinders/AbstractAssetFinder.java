package de.dertoaster.multihitboxlib.assetsynch.assetfinders;

import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractAssetFinder implements Supplier<Set<ResourceLocation>> {

	private ResourceLocation id;
	
	public ResourceLocation getId() {
		return this.id;
	}
	
	void setId(final ResourceLocation id) {
		if (this.id == null) {
			this.id = id;
		}
	}
	
}
