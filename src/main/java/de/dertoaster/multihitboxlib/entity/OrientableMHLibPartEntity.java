package de.dertoaster.multihitboxlib.entity;

import de.dertoaster.multihitboxlib.entity.hitbox.SubPartConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class OrientableMHLibPartEntity<T extends Entity> extends MHLibPartEntity<T> {
    public OrientableMHLibPartEntity(T parent, SubPartConfig properties, EntityDimensions baseSize, Vec3 basePosition, Vec3 pivot) {
        super(parent, properties, baseSize, basePosition, pivot);
    }

    @Override
    protected AABB makeBoundingBox() {
        // TODO: Create orientable BB
        return super.makeBoundingBox();
    }
}
