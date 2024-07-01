package de.dertoaster.multihitboxlib.api.alibplus;

import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animation.AnimationController;
import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface IExtendedGeoAnimatableEntity extends IExtendedGeoAnimatable, GeoEntity {

    /**
     * Overridden modified version of the superclass method for handling custom Geckolib+ behaviour. Do not override.
     * You should use {@link #triggerAnim(String)} and its overloaded variants instead of this.
     *
     * @param controllerName The name of the controller name the animation belongs to, or null to do an inefficient lazy search.
     * @param animName The name of animation to trigger. This needs to have been registered with the controller via {@link AnimationController#triggerableAnim AnimationController.triggerableAnim}.
     */
    @Override
    default void triggerAnim(@Nullable String controllerName, String animName) {
        GeoEntity.super.triggerAnim(controllerName, animName);
    }

    /**
     *
     * @param animName
     */
    default void triggerAnim(String animName) {

    }

    default void triggerAnim(IRawAnimation targetAnim) {

    }
}
