package de.dertoaster.multihitboxlib.api.glibplus;

public class WrappedAnimationController<E extends IExtendedGeoAnimatable> {
    private final E animatable;

    public WrappedAnimationController(E animatable) {
        this.animatable = animatable;
    }


}
