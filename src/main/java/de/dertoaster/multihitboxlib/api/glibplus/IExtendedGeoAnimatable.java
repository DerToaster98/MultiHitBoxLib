package de.dertoaster.multihitboxlib.api.glibplus;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;

/**
 * The base geo animatable interface/implementation for using the FAAL (Functional Animation Abstraction Layer). <br> <br>
 *
 * <b>FAAL</b> (Also known as the <i>Functional Animation Abstraction Layer</i>) is an abstraction layer added by Geckolib+ and intended for general-purpose use-cases that may
 * require lots of boilerplate code and/or unsafe/bug-prone code design. This includes creating complex entities with AI that needs to interact with animations, or
 * executing server-side operations which make use of animation metadata on the server. <br> <br>
 *
 * The idea behind FAAL is to provide safe backend alternatives that can patch into Geckolib and allow for more versatile code design. This opens the door to many new options
 * when it comes to working with animations since assets are enforced from the server, meaning that there's no need to worry about siding animations. This effectively
 * provides a replacement to Geckolib's lightweight design (which gives more control to the client) by enforcing resourcepack assets from the server, as well as providing
 * datapackability for animation-related actions. <br> <br>
 *
 * FAAL works by using its own wrapper classes and custom animation implementations which tick separately from Geckolib's animation system. It provides the
 * option to enforce some more sync between Geckolib and itself by attempting to slightly modify Geckolib's animation ticks if set to do so. By default, FAAL ticks
 * animation progress timers and such on the server, then uses packets to sync progress to the client every time the server updates (usually every tick). This acts as
 * a sort of desync failsafe in case the server ends up lagging. This is also the only bit where FAAL optionally allows for direct modification of Geckolib's animation
 * progress by freezing/slowing it down accordingly. <br> <br>
 *
 * FAAL does <b>not</b> replace Geckolib's animation timer. This is primarily due to the fact that animations are rendered in partial ticks/frames,
 * not server ticks. This effectively means that replacing Geckolib's animation timer would inevitably cause lots of jittering and odd/unspecified
 * behaviour. As such, directly modifying Geckolib animation ticks should be done with caution, bearing this information in mind. <br> <br>
 *
 * That being said, this interface is the base implementation of this system. For more information regarding specific use-cases, such as animatable entities, refer
 * to the classes/interfaces under "See Also".
 *
 * @see IExtendedGeoAnimatableEntity
 */
public interface IExtendedGeoAnimatable extends GeoAnimatable {

    /**
     * Gets the list of {@linkplain WrappedAnimationController WrappedAnimationControllers} for this animatable instance.
     *
     * @return A list of {@linkplain WrappedAnimationController WrappedAnimationControllers} this animatable instance contains.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    <E extends IExtendedGeoAnimatable> ObjectArrayList<WrappedAnimationController<E>> getWrappedAnimationControllers();

    /**
     * Gets the list of {@linkplain IRawAnimation IAnimationBuilders} for this animatable instance.
     *
     * @return A list of {@linkplain IRawAnimation IAnimationBuilders} this animatable instance contains.
     * @param <A> Instances/sublclasses of {@link IRawAnimation}.
     */
    <A extends IRawAnimation> ObjectArrayList<A> getRawAnimations();

    /**
     * Overrides the default registerControllers method of AnimatableManager to auto-register all the {@linkplain WrappedAnimationController#getBaseController() base controllers}
     * of this animatable instance. Do not override.
     *
     * @param controllerRegistrar The registrar to which each base controller should be registered.
     */
    @Override
    default void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        getWrappedAnimationControllers().stream()
                .map(WrappedAnimationController::getBaseController)
                .filter(Objects::nonNull)
                .forEach(controllerRegistrar::add);
    }

    /**
     * Streams through {@link #getWrappedAnimationControllers()} and runs checks against each {@linkplain WrappedAnimationController wrapped controller's name}
     * (which usually defaults to the name of the {@linkplain WrappedAnimationController#getBaseController() base controller}) to see if it matches the given name.
     * Can return null.
     *
     * @param wrappedControllerName The name of the {@link WrappedAnimationController} to run checks against.
     * @return The {@link WrappedAnimationController} that matches the given name, or null if there isn't any.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    @Nullable
    default <E extends IExtendedGeoAnimatable> WrappedAnimationController<E> getWrappedControllerByName(String wrappedControllerName) {
        return (WrappedAnimationController<E>) getWrappedAnimationControllers().stream()
                .filter(targetWrappedController -> targetWrappedController != null && targetWrappedController.getName().equals(wrappedControllerName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Overloaded variant of {@link #getWrappedControllerByName(String)} that returns a {@link AnimationController}. Intended as a casting shortcut method.
     *
     * @param controllerName The name of the {@link WrappedAnimationController} to run checks against and get the {@linkplain AnimationController base controller} from.
     * @return The {@link AnimationController} that matches the given name (via its parent {@link WrappedAnimationController}), or null if there isn't any.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    default <E extends IExtendedGeoAnimatable> AnimationController<E> getControllerByName(String controllerName) {
        return (AnimationController<E>) getWrappedControllerByName(controllerName).getBaseController();
    }

    /**
     * Overloaded variant of {@link #createMappedController(String, AnimationController.AnimationStateHandler)}, which passes {@code animPredicate = animState -> {@link PlayState#CONTINUE}}
     * by default. This is intended as a shortcut method for basic/idiomatic animatable entities.
     *
     * @param controllerName The name of the {@link WrappedAnimationController} to create. Should be unique, so as to avoid runtime exceptions and/or unexpected behaviour.
     * @return A new, usable {@link WrappedAnimationController} (with its transition ticks set to 1 and its AnimStateHandler set to return {@link PlayState#CONTINUE})
     * which is also mapped/added to {@link #getWrappedAnimationControllers()}.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    default <E extends IExtendedGeoAnimatable> WrappedAnimationController<E> createMappedController(String controllerName) {
        return createMappedController(controllerName, animState -> PlayState.CONTINUE, 1);
    }

    /**
     * Overloaded variant of {@link #createMappedController(String, AnimationController.AnimationStateHandler, int)}, which passes {@code animTransitionInterval = 1} by default.
     *
     * @param controllerName The name of the {@link WrappedAnimationController} to create. Should be unique, so as to avoid runtime exceptions and/or unexpected behaviour.
     * @param animPredicate The {@link AnimationController.AnimationStateHandler} used in the {@linkplain WrappedAnimationController#getBaseController() base controller}.
     * @return A new, usable {@link WrappedAnimationController} (with its transition ticks set to 1) which is also mapped/added to {@link #getWrappedAnimationControllers()}.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    default <E extends IExtendedGeoAnimatable> WrappedAnimationController<E> createMappedController(String controllerName, AnimationController.AnimationStateHandler<E> animPredicate) {
        return createMappedController(controllerName, animPredicate, 1);
    }

    /**
     * Creates and adds a new {@link WrappedAnimationController} to {@link #getWrappedAnimationControllers()}. This method (and its overloaded variants) should
     * be used to instantiate new wrapped controllers.
     *
     * @param controllerName The name of the new {@link WrappedAnimationController} to create. Should be unique, so as to avoid runtime exceptions and/or unexpected behaviour.
     * @param animPredicate The {@link AnimationController.AnimationStateHandler} used in the {@linkplain WrappedAnimationController#getBaseController() base controller}.
     *                      This can still be used to handle Geckolib animations in partial ticks/frames on the client.
     * @param animTransitionInterval The ticks between each transition of each animation. More ticks = smoother easing.
     * @return A new, usable {@link WrappedAnimationController} which is also mapped/added to {@link #getWrappedAnimationControllers()}.
     * @param <E> Instances/sublclasses of {@link IExtendedGeoAnimatable}.
     */
    default <E extends IExtendedGeoAnimatable> WrappedAnimationController<E> createMappedController(String controllerName, AnimationController.AnimationStateHandler<E> animPredicate, int animTransitionInterval) {
        WrappedAnimationController<E> mappedController = new WrappedAnimationController<E>((E) this, new AnimationController<E>((E) this, controllerName, animTransitionInterval, animPredicate), animTransitionInterval);
        getWrappedAnimationControllers().add((WrappedAnimationController<IExtendedGeoAnimatable>) mappedController);
        return mappedController;
    }

    /**
     * Ticks each {@link WrappedAnimationController} of this animatable instance. Updated every tick this animatable instance is updated
     * (in {@link Entity#tick()}). Do not override or remove {@code super} call.
     */
    default void tickWrappedAnims() {
        getWrappedAnimationControllers().stream()
                .filter(Objects::nonNull)
                .forEach(WrappedAnimationController::tick);
    }
}