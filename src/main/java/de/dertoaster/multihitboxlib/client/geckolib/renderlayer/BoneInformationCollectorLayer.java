package de.dertoaster.multihitboxlib.client.geckolib.renderlayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BoneInformationCollectorLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {

	public BoneInformationCollectorLayer(GeoRenderer<T> entityRendererIn) {
		super(entityRendererIn);
	}
	
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	
	private int currentTick = -1;
	private boolean _isReRenderFromRecursively = false;
	
	@Override
	public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		if (this._isReRenderFromRecursively) {
			return;
		}
		
		Entity entity = null;
		if (!(animatable instanceof Entity)) {
			if (this.renderer instanceof GeoReplacedEntityRenderer<?, ?> grer) {
				entity = grer.getCurrentEntity();
			}
		} else {
			entity = (Entity)animatable;
		}
		
		// Only collect once per tick!
		if (entity != null && this.currentTick != entity.tickCount && entity.isMultipartEntity()) {
			try {
				IMultipartEntity<?> ime = (IMultipartEntity<?>) entity;
				if (ime.getHitboxProfile().isPresent() && ime.getHitboxProfile().get().syncToModel()) {
					if (ime.getHitboxProfile().get().synchedBones().contains(bone.getName()) || true) {
						final Vec3 worldPos = new Vec3(bone.getWorldPosition().x, bone.getWorldPosition().y, bone.getWorldPosition().z);
						this.calcScales(bone);
						ime.tryAddBoneInformation(bone.getName(), bone.isHidden(), worldPos, new Vec3(this.scaleX, this.scaleY, this.scaleZ));
					}
				}
			} catch(ClassCastException cce) {
				
			}
		}
	}
	
	private void calcScales(GeoBone bone) {
		this.scaleX *= bone.getScaleX();
		this.scaleY *= bone.getScaleY();
		this.scaleZ *= bone.getScaleZ();
	}

	public void onPostRender(Entity animatable) {
		if (!(animatable instanceof LivingEntity le)) {
			return;
		}

		if (this.currentTick != le.tickCount) {
			this.currentTick = le.tickCount;
		}

		this.scaleX = 1;
		this.scaleY = 1;
		this.scaleZ = 1;
		// Custom part rendering
	}

}
