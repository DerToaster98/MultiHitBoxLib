package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityEvent;
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
				
				if (hp.mainHitboxConfig().baseSize().equals(Vec3.ZERO)) {
					return;
				}
				Vec3 customDims = hp.mainHitboxConfig().baseSize();
				event.setNewSize(EntityDimensions.scalable((float)customDims.x, (float)customDims.y), true);
			}
		}
	}
	
}
