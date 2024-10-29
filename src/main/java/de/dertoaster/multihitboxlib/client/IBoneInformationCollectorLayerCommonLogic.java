package de.dertoaster.multihitboxlib.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.dertoaster.multihitboxlib.PartEntityManager;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

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
		if (entity != null && (this.getCurrentTick() == entity.tickCount || this.getCurrentTick() < 0) && PartEntityManager.isMultipartEntity(entity)) {
			try {
				IMultipartEntity<?> ime = (IMultipartEntity<?>) entity;
				if (ime.getHitboxProfile().isPresent() && ime.getHitboxProfile().get().syncToModel()) {
					if (ime.getHitboxProfile().get().synchedBones().contains(this.getBoneName(bone))) {
						final Vec3 worldPos = this.getBoneWorldPosition(bone);
						this.calcScales(bone);
						this.calcRotations(bone);
						ime.tryAddBoneInformation(this.getBoneName(bone), this.isBoneHidden(bone), worldPos, this.getScaleVector(), this.getRotationVector());
						//System.out.println("RenderRecursively: " + worldPos.toString());
						//ime.getPartByName(bone.getName()).get().setPos(worldPos);
					}
				}
			} catch(ClassCastException cce) {
				
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
