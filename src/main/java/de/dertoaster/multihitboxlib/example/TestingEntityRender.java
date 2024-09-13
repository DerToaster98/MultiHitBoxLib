package de.dertoaster.multihitboxlib.example;

import de.dertoaster.multihitboxlib.MHLibMod;
import mod.azure.azurelib.model.DefaultedEntityGeoModel;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class TestingEntityRender extends GeoEntityRenderer<TestingEntity> {
    public TestingEntityRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(MHLibMod.MODID, "doomhunter"), false));
    }
}
