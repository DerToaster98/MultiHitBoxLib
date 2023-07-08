package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.FORGE)
public class EntityEventHandler {

	@SubscribeEvent
	public static void onEntitySizeEvent(EntityEvent.Size event) {
		Entity ent = event.getEntity();
		if (ent instanceof IMultipartEntity<?> ime) {
			if (ime.getHitboxProfile().isPresent()) {
				HitboxProfile hp = ime.getHitboxProfile().get();
				
				if (hp.mainHitboxConfig().baseSize().equals(Vec2.ZERO)) {
					return;
				}
				Vec2 customDims = hp.mainHitboxConfig().baseSize();
				event.setNewSize(EntityDimensions.scalable(customDims.x, customDims.y), true);
			}
		}
	}
	
	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		if (event.getTarget() instanceof LivingEntity && event.getTarget().isMultipartEntity()) {
			if (event.getTarget() instanceof IMultipartEntity<?> ime  && event.getEntity() instanceof ServerPlayer sp) {
				ime.mhLibOnStartTrackingEvent(sp);
			}
		}
	}
	
	@SubscribeEvent
	public static void onStopTracking(PlayerEvent.StopTracking event) {
		if (event.getTarget() instanceof LivingEntity && event.getTarget().isMultipartEntity()) {
			if (event.getTarget() instanceof IMultipartEntity<?> ime  && event.getEntity() instanceof ServerPlayer sp) {
				ime.mhLibOnStopTrackingEvent(sp);
			}
		}
	}
	
}
