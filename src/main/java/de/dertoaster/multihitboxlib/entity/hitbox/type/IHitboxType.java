package de.dertoaster.multihitboxlib.entity.hitbox.type;

import com.mojang.serialization.Codec;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface IHitboxType {

	public default Vec3 getBaseRotation() {
		return Vec3.ZERO;
	}
	
	public Codec<? extends IHitboxType> getType();
	
	public <T extends Entity> MHLibPartEntity<T> createPartEntity(final SubPartConfig config, final T parent, final int partNumber);
	
}
