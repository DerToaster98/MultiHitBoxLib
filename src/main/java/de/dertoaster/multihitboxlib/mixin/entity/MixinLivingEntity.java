package de.dertoaster.multihitboxlib.mixin.entity;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;

import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	
	private static final EntityDataAccessor<Optional<UUID>> CURRENT_MASTER = SynchedEntityData.defineId(MixinLivingEntity.class, EntityDataSerializers.OPTIONAL_UUID);
	
	private final Optional<HitboxProfile> HITBOX_PROFILE;
	
	public MixinLivingEntity(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
		
		this.HITBOX_PROFILE = MHLibDatapackLoaders.getHitboxProfile(pEntityType);
	}

}
