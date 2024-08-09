package de.dertoaster.multihitboxlib.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Anjanath extends Monster implements GeoEntity {

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	private static final RawAnimation ANIM_WALK = RawAnimation.begin().thenPlay("walk");
	private static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenPlay("idle");
	
	public Anjanath(EntityType<? extends Monster> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Override
	public float maxUpStep() {
		return 2.0f;
	}

	public static Builder createAttributes() {
		return Monster.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 16)
				.add(Attributes.MAX_HEALTH, 1)
				.add(Attributes.MOVEMENT_SPEED, 0.25f)
				.add(Attributes.ATTACK_DAMAGE, 5)
				.add(Attributes.ATTACK_KNOCKBACK, 0.1);
	}
	
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar arg0) {
		
	}

}
