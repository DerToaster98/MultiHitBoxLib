package de.dertoaster.multihitboxlib.mixin.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.dertoaster.multihitboxlib.api.IModifiableMultipartEntity;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation.Builder;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import de.dertoaster.multihitboxlib.util.ClientOnlyMethods;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.network.PacketDistributor;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements IMultipartEntity<LivingEntity> {

	@Unique
	protected Map<String, MHLibPartEntity<LivingEntity>> partMap = new HashMap<>();
	@Unique
	protected Map<String, BoneInformation> syncDataMap = new HashMap<>();
	
	@Unique
	private PartEntity<?>[] partArray;
	
	@Unique
	private Optional<CPacketBoneInformation.Builder> boneInformationBuilder = Optional.empty();
	
	@Unique
	private final Queue<UUID> trackerQueue = new LinkedTransferQueue<>();
	@Unique
	private int _mhlibTicksSinceLastSync = 0;
	
	@Override
	public int getTicksSinceLastSync() {
		return this._mhlibTicksSinceLastSync;
	}

	@Override
	public Queue<UUID> getTrackerQueue() {
		return this.trackerQueue;
	}
	
	@Unique
	@Nullable
	private UUID masterUUID = null;
	
	public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}
	
	@Inject(
			method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
			at = @At("TAIL")
			)
	private void mixinConstructor(CallbackInfo ci) {
		// Load base profile
		// this.HITBOX_PROFILE = MHLibDatapackLoaders.getHitboxProfile(this.getType());
		
		if(!this.getHitboxProfile().isPresent()) {
			return;
		}
		
		// Initialize map and array
		int partCount = this.getHitboxProfile().isPresent() ? this.getHitboxProfile().get().partConfigs().size() : 0;
		this.partMap = new Object2ObjectArrayMap<>(partCount);
		this.partArray = new PartEntity<?>[partCount];
		
		if(this.getHitboxProfile().isPresent()) {
			// At last, create the parts themselves
			final BiConsumer<String, MHLibPartEntity<LivingEntity>> storageFunction = (str, part) -> {
				int id = 0;
				while(partArray[id] != null) {
					id++;
				}
				this.partArray[id] = part;
				this.partMap.put(str, part);
			};
			this.createSubPartsFromProfile(this.getHitboxProfile().get(), (LivingEntity)((Object)this), storageFunction);
		}

		if (this.isMultipartEntity() && this.getParts() != null) {
			this.setId(Entity.ENTITY_COUNTER.getAndAdd(this.getParts().length + 1) + 1);
		}
	}
	
	@Override
	public void mhLibOnStartTrackingEvent(ServerPlayer sp) {
		// System.out.println("Adding tracker: " + sp.getUUID() != null ? sp.getUUID().toString() : "NONE");
		if (!this.trackerQueue.contains(sp.getUUID())) {
			this.trackerQueue.add(sp.getUUID());
		}
		if (this.getMasterUUID() == null) {
			this.setMasterUUID(this.trackerQueue.poll());
		}
	}
	
	@Override
	public void mhLibOnStopTrackingEvent(ServerPlayer sp) {
		// System.out.println("Removing tracker: " + sp.getUUID() != null ? sp.getUUID().toString() : "NONE");
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
	
	/*@Inject(
			method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mixinHurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
		if(!(this.HITBOX_PROFILE != null && this.HITBOX_PROFILE.isPresent())) {
			return;
		}
		if (pSource.is(DamageTypes.OUT_OF_WORLD)) {
			return;
		}
		
		if (pSource.isCreativePlayer()) {
			//return;
		}
		
		if (this.HITBOX_PROFILE != null && this.HITBOX_PROFILE.isPresent() && !this.HITBOX_PROFILE.get().mainHitboxConfig().canReceiveDamage()) {
			if (!this.hurtFromPart) {
				cir.setReturnValue(false);
				cir.cancel();
			}
		}
	}*/
	
	// After ticking all parts => call alignment code
	// Bind to interface to potentially cancel auto ticking and alignment
	@Inject(
			method = "aiStep",
			at = @At("TAIL")
	)
	private void mixinAiStep(CallbackInfo ci) {
		if (!this.isMultipartEntity()) {
			return;
		}
		
		// First, send packet if present or handle leader stuff
		this.updateSynching(this);
		
		if (this.level().isClientSide()) {
			return;
		}
		
		// Won't align synched parts
		this.alignSubParts((LivingEntity)(Object)this, this.partMap.values());
		
		// Now, handle synched parts
		if (this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().syncToModel()) {
			this.alignSynchedSubParts((LivingEntity)(Object)this, this.syncDataMap);
		}
	}
	
	// In tick method => intercept and tick the subparts
	@Inject(
			method = "tick",
			at = @At("TAIL")
	)
	private void mixinTick(CallbackInfo ci) {
		if(!this.isMultipartEntity()) {
			return;
		}
		
		this.tickParts((LivingEntity)(Object)this, this.partMap.values());
		
		this._mhlibTicksSinceLastSync++;
	}
	
	@Override
	public void setMasterUUID(UUID id) {
		if (this.level().isClientSide()) {
			// System.out.println("Clientside, not setting master!");
			// TODO: Why are we just setting it here anywa?!
			this.masterUUID = id;
			return;
		}
		this.masterUUID = id;
		// System.out.println("ID SET!");
		/*if(id == null) {
			System.out.println("ID IS NULL!!");
		}
		else {
			System.out.println("Master set to: " + id != null ? id.toString() : "NONE");
		}*/
		SPacketSetMaster masterPacket = new SPacketSetMaster(this);
		MHLibPackets.send(masterPacket, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this));
	}

	@Override
	public synchronized boolean tryAddBoneInformation(String boneName, boolean hidden, Vec3 position, Vec3 scaling, Vec3 rotation) {
		if (!this.level().isClientSide()) {
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
		if (this.boneInformationBuilder.isEmpty()) {
			//return false;
			CPacketBoneInformation.Builder builder = CPacketBoneInformation.builder(this);
			this.boneInformationBuilder = Optional.of(builder);
		}
		
		CPacketBoneInformation.Builder builder = this.boneInformationBuilder.get();
		try {
			builder = builder.addInfo(boneName).hidden(hidden).position(position).scaling(scaling).rotation(rotation).done();
		} catch(IllegalStateException ise) {
			return false;
		}
		
		// Now, if we have the freedom ... directly apply the information...
		if (this.getHitboxProfile().get().trustClient()) {
			Optional<MHLibPartEntity<LivingEntity>> optPart = this.getPartByName(boneName);
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
	
	@Override
	@Nullable
	public UUID getMasterUUID() {
		// Allow change of logic
		if (this instanceof IModifiableMultipartEntity<?> imme) {
			return imme.getMasterUUID();
		}

		return this.masterUUID;
	}

	@Override
	public void processBoneInformation(Map<String, BoneInformation> boneInformation) {
		// Allow change of logic
		if (this instanceof IModifiableMultipartEntity<?> imme) {
			imme.processBoneInformation(boneInformation);
			return;
		}
		// Process the bones...
		for (Map.Entry<String, BoneInformation> entry : boneInformation.entrySet()) {
			Optional<MHLibPartEntity<LivingEntity>> optPart = this.getPartByName(entry.getKey());
			optPart.ifPresent(part -> {
				this.syncDataMap.put(part.getConfigName(), entry.getValue());
			});
		}
		this._mhlibTicksSinceLastSync = 0;
	}
	
	@Override
	public Optional<MHLibPartEntity<LivingEntity>> getPartByName(String name) {
		if (this.partMap == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(this.partMap.getOrDefault(name, null));
	}

	@Override
	public boolean syncWithModel() {
		// Allow change of logic
		if (this instanceof IModifiableMultipartEntity<?> imme) {
			return imme.syncWithModel();
		}
		return this.getHitboxProfile().isPresent() && this.getHitboxProfile().get().syncToModel();
	}

	// Before the constructor gets called => intercept the entityType and modify it's "size" argument to use the mainHitboxSize

	// Intercept defineSynchedData and add our master data

	// Intercept object creation, then create the hitbox profile

	// Add special hurt method => in interface => used for when subparts where damaged

	// Call this after the object has been created => method in interface, needs to be cancellable

	// Override setID to also set the id of the parts
	@Override
	public void setId(int pId) {
		super.setId(pId);
		
		if (this instanceof IModifiableMultipartEntity<?>) {
			return;
		}
		if (this.isMultipartEntity() && this.getParts() != null) {
			for (int i = 0; i < this.getParts().length; i++) {
				this.getParts()[i].setId(pId + i + 1);
			}
		}
	}

	@Override
	public boolean isMultipartEntity() {
		return super.isMultipartEntity() || !this.partMap.values().isEmpty();
	}
	
	@Override
	@Nullable
	public PartEntity<?>[] getParts() {
		if (this instanceof IModifiableMultipartEntity<?>) {
			return super.getParts();
		}
		return this.partArray;
	}

	// Also make sure to modify the result of isMultipartEntity to be correct
	
	@Override
	public float mhlibGetEntityRotationYForPartOffset() {
		if (this instanceof IModifiableMultipartEntity<?> imme) {
			return imme.mhlibGetEntityRotationYForPartOffset();
		}
		
		return this.getYRot();
	}
	
	@Inject(
			method = "isPickable()Z",
			at = @At("RETURN"),
			cancellable = true
	)
	private void mixinIsPickable(CallbackInfoReturnable<Boolean> cir) {
		if(this.getHitboxProfile() != null && this.getHitboxProfile().isPresent()) {
			cir.setReturnValue(cir.getReturnValue() && this.getHitboxProfile().get().mainHitboxConfig().canReceiveDamage());
		}
	}

	@Override
	public Optional<Builder> getBoneInfoBuilder() {
		return this.boneInformationBuilder;
	}
	
	@Override
	public void clearBoneInfoBuilder() {
		this.boneInformationBuilder = Optional.empty();
	}
	
	@Override
	public void setBoneInfoBuilderContent(Builder builder) {
		this.boneInformationBuilder = Optional.ofNullable(builder);
	}
	
}
