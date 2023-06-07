package de.dertoaster.multihitboxlib.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;

public interface IMultipartEntity<T extends Entity> {
	
	public boolean hurt(PartEntity<T> subPart, DamageSource source, float damage);

}
