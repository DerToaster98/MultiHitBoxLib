package de.dertoaster.multihitboxlib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;

@Mixin(EntityRenderer.class)
public interface AccessorEntityRenderer {

	@Accessor
	public EntityRenderDispatcher getEntityRenderDispatcher();
	
}
