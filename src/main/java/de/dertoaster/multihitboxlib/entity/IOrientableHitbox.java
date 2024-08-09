package de.dertoaster.multihitboxlib.entity;

import net.minecraft.world.phys.Vec3;

public interface IOrientableHitbox {
	
	public float getRotationX();
	public float getRotationY();
	public float getRotationZ();

	// Return the radii fo the box here!
	public Vec3 getCenterOffset();
	
}
