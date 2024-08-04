package de.dertoaster.multihitboxlib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public interface IBoneInformationCollectorLayerCommonLogic<T extends Object> {
	
	public int getCurrentTick();
	public void setCurrentTick(int tick);
	
	public void calcScales(T bone);
	public void calcRotations(T bone);
	public String getBoneName(T bone);
	public Vec3 getBoneWorldPosition(T bone);
	public boolean isBoneHidden(T bone);

	public Vec3 getScaleVector();
	public void setScales(int x, int y, int z);
	
	public Vec3 getRotationVector();
	public void setRotations(int x, int y, int z);
	
	public default void onRenderBone(PoseStack poseStack, Entity entity, T bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		// Only collect once per tick!
		if (entity != null && entity.isMultipartEntity() && entity instanceof IMultipartEntity<?> ime && ime.getHitboxProfile().isPresent()) {
			HitboxProfile hitboxProfile = ime.getHitboxProfile().get();
			final Vec3 worldPos = this.getBoneWorldPosition(bone);
			this.calcScales(bone);
			this.calcRotations(bone);
			if (this.getCurrentTick() == entity.tickCount || this.getCurrentTick() < 0) {
				if (hitboxProfile.syncToModel()) {
					if (hitboxProfile.synchedBones().contains(this.getBoneName(bone))) {
						ime.tryAddBoneInformation(this.getBoneName(bone), this.isBoneHidden(bone), worldPos, this.getScaleVector(), this.getRotationVector());
						//System.out.println("RenderRecursively: " + worldPos.toString());
						//ime.getPartByName(bone.getName()).get().setPos(worldPos);
					}
				}
			}
			// After we collected stuff, we set the position directly if we trust the client...
			// Unsafe but honestly, mixins are a thing. Nobody can stop anyone else from installing a clientside mod that moves all hitboxes out of place...
			if (hitboxProfile.trustClient()) {
				Optional<? extends MHLibPartEntity<?>> optPart = ime.getPartByName(this.getBoneName(bone));
				if (optPart.isPresent()) {
					MHLibPartEntity<?> part = optPart.get();
					part.applyInformation(worldPos, this.getScaleVector(), this.getRotationVector(), this.isBoneHidden(bone));
				}
			}
		}
	}
	
	public default void onPostRender(Entity animatable) {
		if (!(animatable instanceof LivingEntity le)) {
			return;
		}

		if (this.getCurrentTick() == le.tickCount || this.getCurrentTick() < 0) {
			this.setCurrentTick(le.tickCount +1);
		}

		this.setScales(1, 1, 1);
		this.setRotations(0, 0, 0);
	}
}
