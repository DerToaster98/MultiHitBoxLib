package de.dertoaster.multihitboxlib.client.azurelib.renderlayer;

import org.joml.Vector3d;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMHLibExtendedRenderLayer;
import de.dertoaster.multihitboxlib.client.IBoneInformationCollectorLayerCommonLogic;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.azure.azurelib.common.api.client.renderer.GeoReplacedEntityRenderer;
import mod.azure.azurelib.common.api.client.renderer.layer.GeoRenderLayer;
import mod.azure.azurelib.common.internal.client.renderer.GeoRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.GeoBone;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class AzurelibBoneInformationCollectorLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> implements IBoneInformationCollectorLayerCommonLogic<GeoBone>, IMHLibExtendedRenderLayer {
	
	private Stack<Tuple<Vector3d, Vector3d>> scaleAndRotationStack = new ObjectArrayList<>();
	private Vector3d currentScaling = new Vector3d(1,1,1);
	private Vector3d currentRotation = new Vector3d(0,0,0);

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
		Vector3d scale = this.getCurrentScaling();
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
		Vector3d scale = this.getCurrentScaling();
		return new Vec3(scale.x, scale.y, scale.z);
	}

	@Override
	public void setScales(int x, int y, int z) {
		Vector3d scale = this.getCurrentScaling();
		scale.x *= x;
		scale.y *= y;
		scale.z *= z;
		/*this.scaleX = x;
		this.scaleY = y;
		this.scaleZ = z;*/
	}

	@Override
	public void calcRotations(GeoBone bone) {
		Vector3d rot = this.getCurrentRotation();
		rot.x += bone.getRotX();
		rot.y += bone.getRotY();
		rot.z += bone.getRotZ();
		/*this.rotX += bone.getRotX();
		this.rotY += bone.getRotY();
		this.rotZ += bone.getRotZ();*/
	}

	@Override
	public Vec3 getRotationVector() {
		Vector3d rot = this.getCurrentRotation();
		return new Vec3(rot.x, rot.y, rot.z);
	}

	@Override
	public void setRotations(int x, int y, int z) {
		Vector3d rot = this.getCurrentRotation();
		rot.x = x;
		rot.y = y;
		rot.z = z;
		/*this.rotX = x;
		this.rotY = y;
		this.rotZ = z;*/
	}
	
	@Override
	public void pushToStack(Vector3d scaling, Vector3d rotation) {
		this.scaleAndRotationStack.push(new Tuple<>(scaling, rotation));
	}

	@Override
	public Tuple<Vector3d, Vector3d> popStack() {
		return this.scaleAndRotationStack.pop();
	}

	@Override
	public Vector3d getCurrentScaling() {
		return this.currentScaling;
	}

	@Override
	public Vector3d getCurrentRotation() {
		return this.currentRotation;
	}

	@Override
	public void applyCurrentValues(Vector3d scaling, Vector3d rotation) {
		this.currentRotation = rotation;
		this.currentScaling = scaling;
	}

	@Override
	public void resetStack() {
		this.scaleAndRotationStack = new ObjectArrayList<>();
	}

}
