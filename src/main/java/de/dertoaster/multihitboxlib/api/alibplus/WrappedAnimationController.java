package de.dertoaster.multihitboxlib.api.alibplus;

import mod.azure.azurelib.core.animation.AnimationController;

/**
 *
 *
 * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
 */
public class WrappedAnimationController<E extends IExtendedGeoAnimatable> {
    protected final E animatable;
    protected final AnimationController<E> baseController;
    protected final String controllerName;
    protected double animTransitionInterval;
    protected double curAnimLength = 0;
    protected double curAnimProgress = 0;
    protected double curTransitionProgress = 0;
    protected IRawAnimation curRawAnim;

    public WrappedAnimationController(E animatable, AnimationController<E> baseController, double animTransitionInterval) {
        this.animatable = animatable;
        this.baseController = baseController;
        this.controllerName = baseController.getName();
        this.animTransitionInterval = animTransitionInterval;
    }

    public WrappedAnimationController(E animatable, AnimationController<E> baseController) {
        this(animatable, baseController, 1);
    }

    public E getAnimatable() {
        return animatable;
    }

    public AnimationController<E> getBaseController() {
        return baseController;
    }

    public String getName() {
        return controllerName;
    }

    public void tick() {

    }

    protected double getSyncedProgress() {
        return 0;
    }

    public static IRawAnimation none() {
        return null;
    }
}
