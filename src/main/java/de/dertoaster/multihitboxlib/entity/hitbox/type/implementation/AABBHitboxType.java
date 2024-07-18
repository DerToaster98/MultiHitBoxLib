package de.dertoaster.multihitboxlib.entity.hitbox.type.implementation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import de.dertoaster.multihitboxlib.entity.hitbox.type.IHitboxType;
import de.dertoaster.multihitboxlib.util.UtilityCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class AABBHitboxType implements IHitboxType {
	
	protected final Vec2 baseSize;
	protected final Vec3 baseRotation; //Only to be used for rendering stuff. Won't change the hitbox as that is still axis alinged
	protected final Vec3 basePosition;
	protected final Vec3 pivotOffset;
	
	public AABBHitboxType(Vec2 size, Vec3 rot, Vec3 pos, Vec3 pivotOffset) {
		this.baseSize = size;
		this.baseRotation = rot;
		this.basePosition = pos;
		this.pivotOffset = pivotOffset;
	}

	public static final Codec<AABBHitboxType> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				UtilityCodecs.VEC2_CODEC.fieldOf("size").forGetter(obj -> obj.baseSize),
				Vec3.CODEC.optionalFieldOf("rotation", Vec3.ZERO).forGetter(obj -> obj.baseRotation),
				Vec3.CODEC.fieldOf("position").forGetter(obj -> obj.basePosition),
				Vec3.CODEC.optionalFieldOf("pivot-offset", Vec3.ZERO).forGetter(obj -> obj.pivotOffset)
			).apply(instance, AABBHitboxType::new);
	});
	
	@Override
	public Codec<? extends IHitboxType> getType() {
		return CODEC;
	}

	@Override
	public <T extends Entity> MHLibPartEntity<T> createPartEntity(SubPartConfig config, T parent, int partNumber) {
		return new MHLibPartEntity<T>(parent, config, EntityDimensions.scalable(this.baseSize.x, this.baseSize.y), this.basePosition);
	}

}
