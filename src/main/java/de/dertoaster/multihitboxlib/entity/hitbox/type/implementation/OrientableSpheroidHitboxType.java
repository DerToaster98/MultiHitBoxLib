package de.dertoaster.multihitboxlib.entity.hitbox.type.implementation;

public class OrientableSpheroidHitboxType {
	
	// Point is within sphere if...
	// - distance to center is <= max-rad of sphere
	// - if distance is <= min-rad of sphere => definitely inside
	// - Otherwise: subtract center loc from point and rotate point by the rotation of the spheroid around the center
	//   If (X^2 / a^2) + (Y^2 / b^2) + (Z^2 / c^2) <= 1, then the point is on or in the sphere 

}
