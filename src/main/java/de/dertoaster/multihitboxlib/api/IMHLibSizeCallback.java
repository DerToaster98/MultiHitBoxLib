package de.dertoaster.multihitboxlib.api;

import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;

public interface IMHLibSizeCallback<T extends Entity> {

    double mhlibGetEntitySizeScale(T entity);

}
