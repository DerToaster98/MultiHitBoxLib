package de.dertoaster.multihitboxlib.api.glibplus;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

/**
 *
 */
public interface IExtendedGeoAnimatable extends GeoAnimatable {

    /**
     * Gets the list of {@linkplain WrappedAnimationController WrappedAnimationControllers} for this animatable instance.
     * @return A list of {@linkplain WrappedAnimationController WrappedAnimationControllers} this animatable instance contains.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    <E extends IExtendedGeoAnimatable> ObjectArrayList<WrappedAnimationController<E>> getWrappedAnimationControllers();

    /**
     *
     * @return
     * @param <A>
     */
    <A extends IAnimationBuilder> ObjectArrayList<A> getAnimationBuilders();
}