package de.dertoaster.multihitboxlib.mixin.entity;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import net.neoforged.neoforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import de.dertoaster.multihitboxlib.api.IMHLibFieldAccessor;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.util.BoneInformation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements IMultipartEntity<LivingEntity>, IMHLibFieldAccessor<LivingEntity> {

	@Unique
	public Map<String, MHLibPartEntity<LivingEntity>> partMap = new HashMap<>();
	@Unique
	public Map<String, BoneInformation> syncDataMap = new HashMap<>();
	
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
		this.mhlibOnConstructor();
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

		this.mhlibAiStep();
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
		
		this.tickParts(this.partMap.values());
	}

	/*
	 * TODO: Rewrite Packets for new Packet system
	 */
	@Override
	public synchronized boolean tryAddBoneInformation(String boneName, boolean hidden, Vec3 position, Vec3 scaling, Vec3 rotation) {
		return IMultipartEntity.super.tryAddBoneInformation(boneName, hidden, position, scaling, rotation);
    }
	
	// Before the constructor gets called => intercept the entityType and modify it's "size" argument to use the mainHitboxSize

	// Intercept defineSynchedData and add our master data

	// Intercept object creation, then create the hitbox profile

	// Add special hurt method => in interface => used for when subparts where damaged

	// Call this after the object has been created => method in interface, needs to be cancellable

	// Override setID to also set the id of the parts
	@Override
	public void setId(int pId) {
		// Attention: First call super, then MHLib!
		super.setId(pId);
		this.mhlibSetID(pId);
	}

	@Override
	public boolean isMultipartEntity() {
		return super.isMultipartEntity() || !this.partMap.values().isEmpty();
	}
	
	@Override
	@Nullable
	public PartEntity<?>[] getParts() {
		return this.mhLibGetParts();
	}

	// Also make sure to modify the result of isMultipartEntity to be correct

	@Inject(
			method = "isPickable()Z",
			at = @At("RETURN"),
			cancellable = true
	)
	private void mixinIsPickable(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(this.mhLibIsPickable(cir.getReturnValue()));
	}

	// MHLib access stuff
	@Override
	public PartEntity<?>[] _mhlibAccess_getPartArray() {
		return this.partArray;
	}

	@Override
	public void _mhlibAccess_setPartArray(final PartEntity<?>[] value) {
		this.partArray = value;
	}

	@Override
	public Queue<UUID> _mhlibAccess_getTrackerQueue() {
		return this.trackerQueue;
	}

	@Override
	public int _mhlibAccess_getTicksSinceLastSynch() {
		return this._mhlibTicksSinceLastSync;
	}

	@Override
	public void _mhlibAccess_setTicksSinceLastSynch(int value) {
		this._mhlibTicksSinceLastSync = value;
	}

	@Override
	public Map<String, MHLibPartEntity<LivingEntity>> _mhlibAccess_getPartMap() {
		return this.partMap;
	}

	@Override
	public void _mhlibAccess_setPartMap(Map<String, MHLibPartEntity<LivingEntity>> value) {
		this.partMap = value;
	}

	@Override
	public Map<String, BoneInformation> _mhlibAccess_getSynchMap() {
		return this.syncDataMap;
	}

	@Override
	public UUID _mhlibAccess_getMasterUUID() {
		return this.masterUUID;
	}

	@Override
	public void _mhlibAccess_setMasterUUID(UUID value) {
		this.masterUUID = value;
	}

	@Override
	public Optional<CPacketBoneInformation.Builder> _mlibAccess_getBoneInfoBuilder() {
		return this.boneInformationBuilder;
	}

	@Override
	public void _mlibAccess_setBoneInfoBuilder(Optional<CPacketBoneInformation.Builder> value) {
		this.boneInformationBuilder = value;
	}

}
