package de.dertoaster.multihitboxlib.api;

import net.minecraft.world.entity.Entity;

@Deprecated(forRemoval = true)
/*
Implement the IMultipartEntity interface directly and override what you need!
 */
public interface IModifiableMultipartEntity<T extends Entity> extends IMultipartEntity<T> {

}
