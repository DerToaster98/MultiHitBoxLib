package de.dertoaster.multihitboxlib.api;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

public interface IMultipartEntity<T extends Entity> {
	
	public default boolean hurt(PartEntity<T> subPart, DamageSource source, float damage) {
		if(!(this instanceof Entity)) {
			throw new IllegalStateException("implementing class must extend " + Entity.class.descriptorString());
		}
		Entity entity = (Entity)this;
		return entity.hurt(source, damage);
	}
	
	public boolean callSuperHurt(DamageSource source, float damage);
	
	public void setMasterUUID(UUID id);
	
	@Nullable
	public UUID getMasterUUID();
	
	public default void processBoneInformation(final Map<String, BoneInformation> boneInformation) {
		// Does nothing by default
	}
	
	public boolean syncWithModel();
	
	public default void createSubPartsFromProfile(final HitboxProfile profile, final T parentEntity, final BiConsumer<String, MHLibPartEntity<T>> storageFunction) {
		int subPartNumber = 0;
		for(SubPartConfig spc : profile.partConfigs()) {
			MHLibPartEntity<T> part = this.createNewPartFrom(spc, parentEntity, subPartNumber);
			subPartNumber++;
			
			storageFunction.accept(spc.name(), part);
		}
	}
	
	public default MHLibPartEntity<T> createNewPartFrom(SubPartConfig spc, final T parentEntity, final int subPartNumber) {
		return new MHLibPartEntity<T>(parentEntity, spc);
	}
	
	public Optional<MHLibPartEntity<T>> getPartByName(final String name);  

	public default void alignSubParts(T entity, final Collection<MHLibPartEntity<T>> parts) {
		final double curX = entity.getX();
		final double curY = entity.getY();
		final double curZ = entity.getZ();
		
		final float rotX = this.mhlibGetEntityRotationXForPartOffset();
		final float rotY = this.mhlibGetEntityRotationYForPartOffset();
		final float rotZ = this.mhlibGetEntityRotationZForPartOffset();
		
		final double entityScale = this.mhlibGetEntitySizeScale(entity);
		
		for(MHLibPartEntity<T> part : parts) {
			Vec3 partOffset = part.getConfigPositionOffset();
			partOffset = partOffset.xRot(rotX);
			partOffset = partOffset.yRot(rotY);
			partOffset = partOffset.zRot(rotZ);
			
			partOffset = partOffset.scale(entityScale);
			
			part.setPos(partOffset.add(curX, curY, curZ));
		}
	}
	
	public default void tickParts(T entity, final Collection<MHLibPartEntity<T>> parts) {
		for(MHLibPartEntity<T> part : parts) {
			part.tick();
		}
	}

	public default double mhlibGetEntitySizeScale(T entity) {
		if(entity instanceof AgeableMob am) {
			if(am.isBaby()) {
				return 0.5D;
			}
		}
		return 1;
	}

	public default float mhlibGetEntityRotationXForPartOffset() {
		return 0;
	}
	
	public default float mhlibGetEntityRotationYForPartOffset() {
		return 0;
	}
	
	public default float mhlibGetEntityRotationZForPartOffset() {
		return 0;
	}
	
	public Optional<HitboxProfile> getHitboxProfile();
	
	public boolean tryAddBoneInformation(String boneName, boolean hidden, Vec3 position, Vec3 scaling);
	
}
