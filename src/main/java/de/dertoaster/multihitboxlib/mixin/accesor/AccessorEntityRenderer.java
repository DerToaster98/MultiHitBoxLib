package de.dertoaster.multihitboxlib.mixin.accesor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderer.class)
@Environment(EnvType.CLIENT)
public interface AccessorEntityRenderer {
    @Accessor("entityRenderDispatcher")
    public EntityRenderDispatcher getEntityRenderDispatcher();
}
