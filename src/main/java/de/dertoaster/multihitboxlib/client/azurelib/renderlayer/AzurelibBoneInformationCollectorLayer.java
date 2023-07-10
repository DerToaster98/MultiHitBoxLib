package de.dertoaster.multihitboxlib.client.azurelib.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.renderer.GeoReplacedEntityRenderer;
import mod.azure.azurelib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class AzurelibBoneInformationCollectorLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> implements IBoneInformationCollectorLayerCommonLogic<GeoBone>{

	public AzurelibBoneInformationCollectorLayer(GeoRenderer<T> entityRendererIn) {
		super(entityRendererIn);
	}
	
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	
	private double rotX = 0;
	private double rotY = 0;
	private double rotZ = 0;
	
	private int currentTick = -1;
	
	@Override
	public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		Entity entity = null;
        if (!(animatable instanceof Entity)) {
            if (this.renderer instanceof GeoReplacedEntityRenderer<?, ?> grer) {
                entity = grer.getCurrentEntity();
            }
        } else {
            entity = (Entity)animatable;
        }
		this.onRenderBone(poseStack, entity, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
	}

	@Override
	public int getCurrentTick() {
		return this.currentTick;
	}

	@Override
	public void setCurrentTick(int tick) {
		this.currentTick = tick;
	}

	@Override
	public void calcScales(GeoBone bone) {
		this.scaleX *= bone.getScaleX();
		this.scaleY *= bone.getScaleY();
		this.scaleZ *= bone.getScaleZ();
	}

	@Override
	public String getBoneName(GeoBone bone) {
		return bone.getName();
	}

	@Override
	public Vec3 getBoneWorldPosition(GeoBone bone) {
        final Vec3 worldPos = new Vec3(bone.getWorldPosition().x, bone.getWorldPosition().y, bone.getWorldPosition().z);
		return worldPos;
	}

	@Override
	public boolean isBoneHidden(GeoBone bone) {
		return bone.isHidden();
	}

	@Override
	public Vec3 getScaleVector() {
		return new Vec3(this.scaleX, this.scaleY, this.scaleZ);
	}

	@Override
	public void setScales(int x, int y, int z) {
		this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;
	}

	@Override
	public void calcRotations(GeoBone bone) {
		this.rotX += bone.getRotX();
		this.rotY += bone.getRotY();
		this.rotZ += bone.getRotZ();
	}

	@Override
	public Vec3 getRotationVector() {
		return new Vec3(this.rotX, this.rotY, this.rotZ);
	}

	@Override
	public void setRotations(int x, int y, int z) {
		this.rotX = x;
		this.rotY = y;
		this.rotZ = z;
	}

}
