package de.dertoaster.multihitboxlib.api;

import java.util.Optional;

import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;

public interface ICustomHitboxProfileSupplier {
	
	public Optional<HitboxProfile> getHitboxProfile();

}
