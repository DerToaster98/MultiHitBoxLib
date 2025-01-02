package de.dertoaster.multihitboxlib.api;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;


public interface IMultipartEntity<T extends Entity> {
	
	public default boolean hurt(PartEntity<T> subPart, DamageSource source, float damage) {
		if(!(this instanceof Entity)) {
			throw new IllegalStateException("implementing class must extend " + Entity.class.descriptorString());
		}
		Entity entity = (Entity)this;
		return entity.hurt(source, damage);
	}
	
	public boolean callSuperHurt(DamageSource source, float damage);
	
	public default void setMasterUUID(UUID id) {
		if(!(this instanceof Entity)) {
			throw new IllegalStateException("implementing class must extend " + Entity.class.descriptorString());
		}
		Entity entity = (Entity)this;
		if (this instanceof IMHLibFieldAccessor<?> access) {
			if (entity.level().isClientSide()) {
				// System.out.println("Clientside, not setting master!");
				// TODO: Why are we just setting it here anywa?!
				access._mhlibAccess_setMasterUUID(id);
				return;
			}
			access._mhlibAccess_setMasterUUID(id);
			// System.out.println("ID SET!");
			/*if(id == null) {
				System.out.println("ID IS NULL!!");
			}
			else {
				System.out.println("Master set to: " + id != null ? id.toString() : "NONE");
			}*/
			SPacketSetMaster masterPacket = new SPacketSetMaster(this);
			//MHLibPackets.send(masterPacket, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity));
			PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, masterPacket);
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	
	@Nullable
	public default UUID getMasterUUID() {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			return access._mhlibAccess_getMasterUUID();
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	
	public default void processBoneInformation(final Map<String, BoneInformation> boneInformation) {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			// Process the bones...
			for (Map.Entry<String, BoneInformation> entry : boneInformation.entrySet()) {
				Optional<MHLibPartEntity<T>> optPart = this.getPartByName(entry.getKey());
				optPart.ifPresent(part -> {
					access._mhlibAccess_getSynchMap().put(part.getConfigName(), entry.getValue());
				});
			}
			access._mhlibAccess_setTicksSinceLastSynch(0);
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	
	public default boolean syncWithModel() {
		return this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().syncToModel();
	}
	
	public default void createSubPartsFromProfile(final HitboxProfile profile, final T parentEntity, final BiConsumer<String, MHLibPartEntity<T>> storageFunction) {
		int subPartNumber = 0;
		for(SubPartConfig spc : profile.partConfigs()) {
			MHLibPartEntity<T> part = this.createNewPartFrom(spc, parentEntity, subPartNumber);
			subPartNumber++;
			
			storageFunction.accept(spc.name(), part);
		}
	}
	
	public default MHLibPartEntity<T> createNewPartFrom(SubPartConfig spc, final T parentEntity, final int subPartNumber) {
		return spc.hitboxType().createPartEntity(spc, parentEntity, subPartNumber);
	}
	
	public default Optional<MHLibPartEntity<T>> getPartByName(final String name) {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			if (access._mhlibAccess_getPartMap() == null) {
				return Optional.empty();
			}
			return Optional.ofNullable((MHLibPartEntity<T>) access._mhlibAccess_getPartMap().getOrDefault(name, null));
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	
	public Queue<UUID> getTrackerQueue();
	public int getTicksSinceLastSync();
	
	public default Optional<CPacketBoneInformation.Builder> getBoneInfoBuilder() {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			return access._mlibAccess_getBoneInfoBuilder();
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	public default void clearBoneInfoBuilder() {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			access._mlibAccess_setBoneInfoBuilder(Optional.empty());
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}
	public default void setBoneInfoBuilderContent(CPacketBoneInformation.Builder builder) {
		if (this instanceof IMHLibFieldAccessor<?> access) {
			access._mlibAccess_setBoneInfoBuilder(Optional.ofNullable(builder));
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}

	public default void alignSubParts(T entity, final Collection<MHLibPartEntity<T>> parts) {
		final double curX = entity.getX();
		final double curY = entity.getY();
		final double curZ = entity.getZ();
		
		final float rotX = (float) (this.mhlibGetEntityRotationXForPartOffset() + Math.toRadians(entity.getXRot()));
		// TODO: Unsure what to do with this as this could mess up the position if we just add the y rot to it...
		// ... Otherwise this is for non synched parts, so it should be alright
		final float rotY = (float) (this.mhlibGetEntityRotationYForPartOffset() + Math.toRadians(entity.getYRot()));
		final float rotZ = this.mhlibGetEntityRotationZForPartOffset();
		
		final double entityScale = this.mhlibGetEntitySizeInternally(entity);
		
		for(MHLibPartEntity<T> part : parts) {
			if (this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().synchedBones().contains(part.getConfigName())) {
				continue;
			}
			Vec3 partOffset = part.getConfigPositionOffset();
			partOffset = partOffset.xRot(rotX);
			partOffset = partOffset.yRot(rotY);
			partOffset = partOffset.zRot(rotZ);
			
			partOffset = partOffset.scale(entityScale);

			partOffset = partOffset.add(curX, curY, curZ);
			// Subtract pivot so the position is correct
			partOffset = partOffset.subtract(part.getPivot());

			part.setScaling(new Vec3(entityScale, entityScale, entityScale));
			part.setPos(partOffset);
		}
	}
	
	public default void alignSynchedSubParts(T entity, final BiFunction<String, BoneInformation, BoneInformation> infoRetrievalFunction) {
		final double curX = entity.getX();
		final double curY = entity.getY();
		final double curZ = entity.getZ();

		// Rotations for the fallback position
		final float rotX = (float) (this.mhlibGetEntityRotationXForPartOffset() + Math.toRadians(entity.getXRot()));
		// TODO: Unsure what to do with this as this could mess up the position if we just add the y rot to it...
		// ... Otherwise this is for non synched parts, so it should be alright
		final float rotY = (float) (this.mhlibGetEntityRotationYForPartOffset() + Math.toRadians(entity.getYRot()));
		final float rotZ = this.mhlibGetEntityRotationZForPartOffset();

		final double entityScale = this.mhlibGetEntitySizeInternally(entity);
		
		// Evaluate model data
		for (String syncedBone : this.getHitboxProfile().get().synchedBones()) {
			//System.out.println("Synching bone: " + syncedBone);
			Optional<MHLibPartEntity<T>> optPart = this.getPartByName(syncedBone);
			if (optPart.isEmpty()) {
				//System.out.println("No part found!");
				continue;
			}
			MHLibPartEntity<T> part = optPart.get();
			
			Vec3 partOffset = part.getConfigPositionOffset();
			partOffset = partOffset.xRot(rotX);
			partOffset = partOffset.yRot(rotY);
			partOffset = partOffset.zRot(rotZ);

			partOffset = partOffset.scale(entityScale);
			
			//System.out.println("SynchedDataMap contents: " + this.syncDataMap.keySet().toString());
			BoneInformation bi = infoRetrievalFunction.apply(syncedBone, new BoneInformation(
					syncedBone, 
					false, 
					part.getConfig() != null ? partOffset.add(curX, curY, curZ) : Vec3.ZERO, 
					BoneInformation.DEFAULT_SCALING,
					part.getConfig() != null ? part.getConfig().hitboxType().getBaseRotation() : Vec3.ZERO
			));
			bi = bi.scale(entityScale);
			//System.out.println("Sync data: " + bi.toString());

			part.applyInformation(bi);
		}
	}

	default double mhlibGetEntitySizeInternally(T entity) {
		if (this instanceof IMHLibSizeCallback sc) {
			return sc.mhlibGetEntitySizeScale(entity);
		} else {
			if(entity instanceof AgeableMob am) {
				if(am.isBaby()) {
					return 0.5D;
				}
			}
			return 1;
		}
	}

	public default <E extends Entity & IMultipartEntity<?>> void mhlibAiStep() {
		if (this instanceof IMHLibFieldAccessor access) {
			E e = (E)this;
			// First, send packet if present or handle leader stuff
			this.updateSynching(e);

			if (e.level().isClientSide()) {
				return;
			}

			// Won't align synched parts
			this.alignSubParts((T)(Object)this, access._mhlibAccess_getPartMap().values());

			// Now, handle synched parts
			if (this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().syncToModel()) {
				Map<String, BoneInformation> syncMap = access._mhlibAccess_getSynchMap();
				this.alignSynchedSubParts((T)(Object)this, syncMap::getOrDefault);
				access._mhlibAccess_getSynchMap().clear();
			}

		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}

	/*
	 * Updates and resets the master entity on server side.
	 * On client, if a boneinfobuilder is present, it compiles a packet and sends it to the server, afterwise, the builder gets cleared
	 */
	public default <E extends Entity & IMultipartEntity<?>> void updateSynching(E entity) {
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
	
	public default void tickParts(final Collection<MHLibPartEntity<T>> parts) {
		for(MHLibPartEntity<T> part : parts) {
			part.tick();
		}
		if (this instanceof IMHLibFieldAccessor access) {
			access._mhlibAccess_setTicksSinceLastSynch(access._mhlibAccess_getTicksSinceLastSynch() + 1);
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
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
	
	public default boolean tryAddBoneInformation(String boneName, boolean hidden, Vec3 position, Vec3 scaling, Vec3 rotation) {
		IMHLibFieldAccessor access = (IMHLibFieldAccessor) this;
		Entity entity = (Entity)this;

		if (!entity.level().isClientSide()) {
			return false;
		}
		UUID myMaster = this.getMasterUUID();
		if (myMaster == null || !myMaster.equals(ClientOnlyMethods.getClientPlayer().getUUID())) {
			return false;
		}
		if (this.getHitboxProfile().isEmpty()) {
			return false;
		}
		if (this.getHitboxProfile().isPresent() && !this.getHitboxProfile().get().syncToModel()) {
			return false;
		}
		if (access._mlibAccess_getBoneInfoBuilder().isEmpty()) {
			//return false;
			CPacketBoneInformation.Builder builder = CPacketBoneInformation.builder(entity);
			access._mlibAccess_setBoneInfoBuilder(Optional.of(builder));
		}

		Optional<CPacketBoneInformation.Builder> optBuilder = access._mlibAccess_getBoneInfoBuilder();
		CPacketBoneInformation.Builder builder = optBuilder.get();
		try {
			builder = builder.addInfo(boneName).hidden(hidden).position(position).scaling(scaling).rotation(rotation).done();
		} catch(IllegalStateException ise) {
			return false;
		}

		// Now, if we have the freedom ... directly apply the information...
		if (this.getHitboxProfile().get().trustClient()) {
			Optional<MHLibPartEntity<T>> optPart = this.getPartByName(boneName);
			if (optPart.isPresent() && optPart.get().isSynched()) {
				final double distance = Math.abs(optPart.get().position().distanceToSqr(position));
				if (distance <= optPart.get().getConfig().maxDeviationFromServer()) {
					// You may
					optPart.get().setPositionAndRotationDirect(position.x(), position.y(), position.z(), (float)rotation.y(), (float)rotation.x(), this.getHitboxProfile().get().synchedPartUpdateSteps());
				}
			}
		}

		return true;
	}
	
	public default void mhLibOnStartTrackingEvent(ServerPlayer sp) {
		IMHLibFieldAccessor access = (IMHLibFieldAccessor) this;
		// System.out.println("Adding tracker: " + sp.getUUID() != null ? sp.getUUID().toString() : "NONE");
		if (!access._mhlibAccess_getTrackerQueue().contains(sp.getUUID())) {
			access._mhlibAccess_getTrackerQueue().add(sp.getUUID());
		}
		if (this.getMasterUUID() == null) {
			Queue<UUID> q = access._mhlibAccess_getTrackerQueue();
			this.setMasterUUID(q.poll());
		}
	}
	
	public default void mhLibOnStopTrackingEvent(ServerPlayer sp) {
		if (this.getTrackerQueue().contains(sp.getUUID())) {
			this.getTrackerQueue().remove(sp.getUUID());
		}
		if (this.getMasterUUID() != null && this.getMasterUUID().equals(sp.getUUID())) {
			this.setMasterUUID(null);
		}
		if (this.getMasterUUID() == null && this.getTrackerQueue().size() > 0) {
			this.setMasterUUID(this.getTrackerQueue().poll());
		}
	}
	
	public default void processSetMasterPacket(final SPacketSetMaster packet) {
		if(this instanceof Entity entity) {
			if (entity.level().isClientSide()) {
				this.setMasterUUID(packet.masterUUID());
			}
		} else {
			throw new IllegalStateException("This interface may only be implemented on entities!");
		}
	}

	public default void mhlibSetID(int id) {
		if (this instanceof Entity entity) {
			// NEVER DO THIS, this will cause a infintie recursion loop!
			//entity.setId(id);

			if (entity.isMultipartEntity() && entity.getParts() != null) {
				for (int i = 0; i < entity.getParts().length; i++) {
					entity.getParts()[i].setId(id + i + 1);
				}
			}
		} else {
			throw new IllegalStateException("Interface must be implemented on at least Entity.class!");
		}
	}

	public default boolean mhLibIsPickable(boolean entityClassResult) {
		if(this.getHitboxProfile() != null && this.getHitboxProfile().isPresent()) {
			return entityClassResult && this.getHitboxProfile().get().mainHitboxConfig().canReceiveDamage();
		}
		return entityClassResult;
	}

	public default PartEntity<?>[] mhLibGetParts() {
		if (this instanceof IMHLibFieldAccessor access) {
			return access._mhlibAccess_getPartArray();
		} else {
			throw new IllegalStateException("Access interface not implemented");
		}
	}

	public default void mhlibOnConstructor() {
		IMHLibFieldAccessor access = (IMHLibFieldAccessor) this;
		Entity entity = (Entity) this;
		// Load base profile
		// this.HITBOX_PROFILE = MHLibDatapackLoaders.getHitboxProfile(this.getType());

		if(!this.getHitboxProfile().isPresent()) {
			return;
		}

		// Initialize map and array
		int partCount = this.getHitboxProfile().isPresent() ? this.getHitboxProfile().get().partConfigs().size() : 0;

		Map<String, MHLibPartEntity<T>> partMap = new Object2ObjectArrayMap<>(partCount);
		PartEntity<?>[] partArray = new PartEntity<?>[partCount];

		if(this.getHitboxProfile().isPresent()) {
			// At last, create the parts themselves
			final BiConsumer<String, MHLibPartEntity<T>> storageFunction = (str, part) -> {
				int id = 0;
				while(partArray[id] != null) {
					id++;
				}
				partArray[id] = part;
				partMap.put(str, part);
			};
			this.createSubPartsFromProfile(this.getHitboxProfile().get(), (T)((Object)this), storageFunction);
		}

		access._mhlibAccess_setPartMap(partMap);
		access._mhlibAccess_setPartArray(partArray);

		if (entity.isMultipartEntity() && entity.getParts() != null) {
			// TODO: AccessTransformer
			entity.setId(Entity.ENTITY_COUNTER.getAndAdd(entity.getParts().length + 1) + 1);
		}
	}
}
