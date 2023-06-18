package de.dertoaster.multihitboxlib.mixin.geckolib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.client.MHLibClient;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class MixinGeoEntityRenderer<T extends Entity & GeoAnimatable> extends EntityRenderer<T> implements GeoRenderer<T> {
	
	private double scaleX = 1;
	private double scaleY = 1;
	private double scaleZ = 1;
	
	private int currentTick = -1;
	private boolean _isReRenderFromRecursively = false;
	
	protected MixinGeoEntityRenderer(Context pContext) {
		super(pContext);
	}
	
	private void mixinRenderRecursively() {
		
	}
	
	@Inject(
			remap = false,
			method = "renderCubesOfBone("
					+ "Lcom/mojang/blaze3d/vertex/PoseStack;"
					+ "Lsoftware/bernie/geckolib/cache/object/GeoBone;"
					+ "Lcom/mojang/blaze3d/vertex/VertexConsumer;"
					+ "I"
					+ "I"
					+ "F"
					+ "F"
					+ "F"
					+ "F"
					+ ")V",
			at = @At("HEAD")
	)
	private void mixinRenderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
			   int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
		if (this._isReRenderFromRecursively) {
			return;
		}
		
		T animatable = this.getAnimatable();
		// Only collect once per tick!
		if (this.currentTick != animatable.tickCount && animatable instanceof IMultipartEntity<?> ime) {
			if (ime.getHitboxProfile().isPresent() && ime.getHitboxProfile().get().syncToModel()) {
				if (ime.getHitboxProfile().get().synchedBones().contains(bone.getName())) {
					final Vec3 worldPos = new Vec3(bone.getWorldPosition().x, bone.getWorldPosition().y, bone.getWorldPosition().z);
					this.calcScales(bone);
					ime.tryAddBoneInformation(bone.getName(), bone.isHidden(), worldPos, new Vec3(this.scaleX, this.scaleY, this.scaleZ));
				}
			}
		}
	}
	
	private void calcScales(GeoBone bone) {
		this.scaleX *= bone.getScaleX();
		this.scaleY *= bone.getScaleY();
		this.scaleZ *= bone.getScaleZ();
	}

	@Inject(
			remap = false,
			method = "postRender("
					+ "Lcom/mojang/blaze3d/vertex/PoseStack;"
					+ "Ljava/lang/Object;"
					+ "Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;"
					+ "Lnet/minecraft/client/renderer/MultiBufferSource;"
					+ "Lcom/mojang/blaze3d/vertex/VertexConsumer;"
					+ "Z"
					+ "F"
					+ "I"
					+ "I"
					+ "F"
					+ "F"
					+ "F"
					+ "F"
					+ ")V",
			at = @At("TAIL")
	)
	private void mixinPostRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
		if (isReRender) {
			return;
		}
		// Custom part rendering
		if (animatable.isMultipartEntity() &&  animatable instanceof IMultipartEntity<?> ime && animatable.getParts() != null && animatable.getParts().length > 0) {
			for(PartEntity<?> part : animatable.getParts()) {
				if(part instanceof MHLibPartEntity<?> mhlpe) {
					if (mhlpe.hasCustomRenderer()) {
						EntityRenderer<? extends MHLibPartEntity<? extends Entity>> renderer = MHLibClient.getRendererFor(mhlpe, this.entityRenderDispatcher);
						if (renderer == null) {
							continue;
						}

						float f = Mth.lerp(partialTick, mhlpe.yRotO, mhlpe.getYRot());

						poseStack.pushPose();

						Vec3 translate = mhlpe.position().subtract(animatable.position());
						poseStack.translate(translate.x(), translate.y(), translate.z());

						((EntityRenderer<MHLibPartEntity<?>>) renderer).render(mhlpe, f, partialTick, poseStack, bufferSource, packedLight);

						poseStack.popPose();
					} else {
						continue;
					}
				}
			}
		}
		
		if (this.currentTick != animatable.tickCount) {
			this.currentTick = animatable.tickCount;
		}
		
		this.scaleX = 1;
		this.scaleY = 1;
		this.scaleZ = 1;
	}

}
