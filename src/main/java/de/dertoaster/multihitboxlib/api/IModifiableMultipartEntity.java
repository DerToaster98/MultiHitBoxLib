package de.dertoaster.multihitboxlib.api;

import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import net.minecraft.world.entity.Entity;

public interface IModifiableMultipartEntity<T extends Entity> extends IMultipartEntity<T> {

    public default MHLibPartEntity<? extends T> createSubPart(final T parentEntity, final SubPartConfig properties) {
        return new MHLibPartEntity<T>(parentEntity, properties);
    }

}