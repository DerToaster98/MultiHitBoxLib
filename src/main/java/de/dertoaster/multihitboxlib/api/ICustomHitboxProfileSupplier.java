package de.dertoaster.multihitboxlib.api;


import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;

import java.util.Optional;

public interface ICustomHitboxProfileSupplier {

    public Optional<HitboxProfile> getHitboxProfile();

}
