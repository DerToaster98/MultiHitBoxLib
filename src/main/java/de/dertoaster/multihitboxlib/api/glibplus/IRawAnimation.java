package de.dertoaster.multihitboxlib.api.glibplus;

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
     * Gets the animatable owner of this animation instance.
     *
     * @return The animatable owner of this animation instance.
     */
    IExtendedGeoAnimatableEntity getAnimatableOwner();
}
