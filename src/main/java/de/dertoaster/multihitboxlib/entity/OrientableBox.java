package de.dertoaster.multihitboxlib.entity;

import net.minecraft.world.phys.AABB;

public class OrientableBox extends AABB {

    protected final double rotX;
    protected final double rotY;
    protected final double rotZ;

    public OrientableBox(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2, double rotX, double rotY, double rotZ) {
        super(pX1, pY1, pZ1, pX2, pY2, pZ2);
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }
}
