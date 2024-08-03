package de.dertoaster.multihitboxlib.api.glibplus;

import software.bernie.geckolib.animation.Animation;

public interface IRawAnimation {

    /**
     * Gets the name of this animation instance. Should match the name inside the animation json file.
     *
     * @return The name of this animation instance.
     */
    String getName();

    /**
     * Gets the length of this animation instance, in ticks.
     *
     * @return The length (in ticks) of this animation instance.
     */
    double getAnimLength();
    /**
     * Gets the current progress (in ticks) of this animation instance.
     *
     * @return The current tick progress of this animation instance.
     */
    double getCurAnimProgress();
    /**
     * Gets the speed modifier (in ticks) for this animation instance, usually defaults to {@code 1}.
     *
     * @return The speed modifier (in ticks) for this animation instance.
     */
    double getAnimSpeedModifier();

    /**
     * Gets the animatable owner of this animation instance.
     *
     * @return The animatable owner of this animation instance.
     */
    IExtendedGeoAnimatableEntity getAnimatableOwner();

    /**
     * Gets the loop type of this animation instance.
     *
     * @return The loop type of this animation instance.
     */
    Animation.LoopType getLoopType();

    /**
     * Plays this animation instance (leniently if {@code forceAnim} is {@code false}). Leniently-played animations will
     * wait for any animation(s) currently playing in the owner {@link WrappedAnimationController} of this animation instance
     * to stop.
     *
     * @param forceAnim Whether this animation instance should take priority and override any currently playing animation(s) in the owner {@link WrappedAnimationController}.
     */
    void playAnimation(boolean forceAnim);

    /**
     * Stops this animation instance (leniently of {@code lenientStop} is {@code true}). Leniently-stopped animations will
     * attempt to stop <i>after</i> this animation instance is done (regardless of loop type). Otherwise, it will normally/forcefully
     * stop this animation instance.
     *
     * @param lenientStop Whether this animation should leniently stop if its loop type is {@link software.bernie.geckolib.core.animation.Animation.LoopType#PLAY_ONCE}.
     */
    void stopAnimation(boolean lenientStop);
}
