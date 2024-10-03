package de.dertoaster.multihitboxlib.client.azurelib.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.alibplus.MHLibExtendedGeoLayer;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import mod.azure.azurelib.common.api.client.renderer.GeoReplacedEntityRenderer;
import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import mod.azure.azurelib.core.animatable.GeoAnimatable;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class AzurelibBoneInformationCollectorLayer<T extends GeoAnimatable> extends MHLibExtendedGeoLayer<T> implements IBoneInformationCollectorLayerCommonLogic<GeoBone>{

	public AzurelibBoneInformationCollectorLayer(GeoRenderer<T> entityRendererIn) {
		super(entityRendererIn);
	}
	
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
		Vector3d scale = this.getCurrentScalingEntry();
		scale.x *= bone.getScaleX();
		scale.y *= bone.getScaleY();
		scale.z *= bone.getScaleZ();
		//this.scaleX *= bone.getScaleX();
		//this.scaleY *= bone.getScaleY();
		//this.scaleZ *= bone.getScaleZ();
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
		Vector3d scale = this.getCurrentScalingEntry();
		return new Vec3(scale.x, scale.y, scale.z);
	}

	@Override
	public void setScales(int x, int y, int z) {
		Vector3d scale = this.getCurrentScalingEntry();
		scale.x *= x;
		scale.y *= y;
		scale.z *= z;
		/*this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;*/
	}

	@Override
	public void calcRotations(GeoBone bone) {
		Vector3d rot = this.getCurrentRotationEntry();
		rot.x += bone.getRotX();
		rot.y += bone.getRotY();
		rot.z += bone.getRotZ();
		/*this.rotX += bone.getRotX();
		this.rotY += bone.getRotY();
		this.rotZ += bone.getRotZ();*/
	}

	@Override
	public Vec3 getRotationVector() {
		Vector3d rot = this.getCurrentRotationEntry();
		return new Vec3(rot.x, rot.y, rot.z);
	}

	@Override
	public void setRotations(int x, int y, int z) {
		Vector3d rot = this.getCurrentRotationEntry();
		rot.x = x;
		rot.y = y;
		rot.z = z;
		/*this.rotX = x;
		this.rotY = y;
		this.rotZ = z;*/
	}

}
