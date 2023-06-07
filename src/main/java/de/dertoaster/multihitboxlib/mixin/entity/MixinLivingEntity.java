package de.dertoaster.multihitboxlib.mixin.entity;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements IMultipartEntity<Entity>{
	
	private static final EntityDataAccessor<Optional<UUID>> CURRENT_MASTER = SynchedEntityData.defineId(MixinLivingEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	
	private final Optional<HitboxProfile> HITBOX_PROFILE;
	
	private EntityDimensions _mhlib_MainHitboxDimensions;
	
	public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		
		this.HITBOX_PROFILE = MHLibDatapackLoaders.getHitboxProfile(pEntityType);
	}
	
	// Before the constructor gets called => intercept the entityType and modify it's "size" argument to use the mainHitboxSize
	
	// In tick method => intercept and tick the subparts
	// After that => call alignment code
	// Bind to interface to potentially cancel auto ticking and alignment
	
	// Intercept defineSynchedData and add our master data
	
	// Intercept object creation, then create the hitbox profile
	
	// Add special hurt method => in interface => used for when subparts where damaged
	
	// Call this after the object has been created => method in interface, needs to be cancellable
	
	// Override setID to also set the id of the parts
	
	// Also make sure to modify the result of isMultipartEntity to be correct

}
