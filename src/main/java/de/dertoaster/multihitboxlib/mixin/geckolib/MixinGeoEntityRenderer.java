package de.dertoaster.multihitboxlib.mixin.geckolib;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class MixinGeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {
	
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	
	private double rotX = 0.0D;
	private double rotY = 0.0D;
	private double rotZ = 0.0D;

	protected MixinGeoEntityRenderer(Context pContext) {
		super(pContext);
	}

}
