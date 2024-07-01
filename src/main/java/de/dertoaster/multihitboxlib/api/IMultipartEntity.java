package de.dertoaster.multihitboxlib.api;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

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
	
	public Queue<UUID> getTrackerQueue();
	public int getTicksSinceLastSync();
	
	public Optional<CPacketBoneInformation.Builder> getBoneInfoBuilder();
	public void clearBoneInfoBuilder();
	public void setBoneInfoBuilderContent(CPacketBoneInformation.Builder builder);

	public default void alignSubParts(T entity, final Collection<MHLibPartEntity<T>> parts) {
		final double curX = entity.getX();
		final double curY = entity.getY();
		final double curZ = entity.getZ();
		
		final float rotX = this.mhlibGetEntityRotationXForPartOffset();
		final float rotY = this.mhlibGetEntityRotationYForPartOffset();
		final float rotZ = this.mhlibGetEntityRotationZForPartOffset();
		
		final double entityScale = this.mhlibGetEntitySizeScale(entity);
		
		for(MHLibPartEntity<T> part : parts) {
			if (this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().synchedBones().contains(part.getConfigName())) {
				continue;
			}
			Vec3 partOffset = part.getConfigPositionOffset();
			partOffset = partOffset.xRot(rotX);
			partOffset = partOffset.yRot(rotY);
			partOffset = partOffset.zRot(rotZ);
			
			partOffset = partOffset.scale(entityScale);
			
			part.setPos(partOffset.add(curX, curY, curZ));
		}
	}
	
	public default <E extends Entity & IMultipartEntity<?>> void alignSynchedSubParts(E entity) {
		if (!entity.level().isClientSide()) {
			// If you already have a master, let's check them...
			// If there was no packet for quite some time => elect a new master
			// System.out.println("Checking master answer time...");
			if (this.getMasterUUID() != null && this.getTicksSinceLastSync() >= 10) {
				// System.out.println("Master found and too long answer time!");
				if (this.getTrackerQueue().contains(this.getMasterUUID())) {
					this.getTrackerQueue().remove(this.getMasterUUID());
				}
				this.getTrackerQueue().add(this.getMasterUUID());
				this.setMasterUUID(null);
				// System.out.println("Master was reset!");
			}
			
			// If you don't have a master anyway, elect a new one
			if (this.getMasterUUID() == null) {
				// System.out.println("Setting new master...");
				if (!this.getTrackerQueue().isEmpty()) {
					this.setMasterUUID(this.getTrackerQueue().poll());
				}
			}
		}
		else {
			// System.out.println("Beginning bone information collection...");
			if (this.getBoneInfoBuilder().isPresent()) {
				// Send packet
				// System.out.println("Sending bone information...");
				CPacketBoneInformation packet = this.getBoneInfoBuilder().get().build();
				packet.send();
				this.clearBoneInfoBuilder();
			} else {
				// System.out.println("creating new packet");
				CPacketBoneInformation.Builder builder = CPacketBoneInformation.builder(entity);
				this.setBoneInfoBuilderContent(builder);
			}
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
	
	public default Optional<HitboxProfile> getHitboxProfile() {
		if (this instanceof ICustomHitboxProfileSupplier ichps) {
			Optional<HitboxProfile> resTmp = ichps.getHitboxProfile();
			if (resTmp != null) {
				return resTmp;
			}
		}
		if (this instanceof Entity ent && ent.level() != null) {
			EntityType<?> type = ent.getType();
			return MHLibDatapackLoaders.getHitboxProfile(type, ent.level().registryAccess());
		}
		return Optional.empty();
	}
	
	public boolean tryAddBoneInformation(String boneName, boolean hidden, Vec3 position, Vec3 scaling, Vec3 rotation);
	
	public void mhLibOnStartTrackingEvent(ServerPlayer sp);
	
	public void mhLibOnStopTrackingEvent(ServerPlayer sp);
	
	public default void processSetMasterPacket(final SPacketSetMaster packet) {
		if(this instanceof Entity entity) {
			if (entity.level().isClientSide()) {
				this.setMasterUUID(packet.getMasterUUID());
			}
		} else {
			throw new IllegalStateException("This interface may only be implemented on entities!");
		}
	}
}
