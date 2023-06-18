package de.dertoaster.multihitboxlib.client;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.client.geckolib.renderlayer.BoneInformationCollectorLayer;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.mixin.accessor.AccessorEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.FORGE)
public class GeckolibEventHandler {
	
	@SubscribeEvent
	public static void onPostRender(GeoRenderEvent.Entity.Post event) {
		Entity animatable = event.getEntity();
		if (!(animatable instanceof GeoEntity && animatable instanceof LivingEntity le)) {
			return;
		}
		if (le.isMultipartEntity() &&  animatable instanceof IMultipartEntity<?> ime && le.getParts() != null && le.getParts().length > 0) {
			for(PartEntity<?> part : le.getParts()) {
				if(part instanceof MHLibPartEntity<?> mhlpe) {
					if (mhlpe.hasCustomRenderer()) {
						EntityRenderer<? extends MHLibPartEntity<? extends Entity>> renderer = MHLibClient.getRendererFor(mhlpe, ((AccessorEntityRenderer)event.getRenderer()).getEntityRenderDispatcher());
						if (renderer == null) {
							continue;
						}

						float f = Mth.lerp(event.getPartialTick(), mhlpe.yRotO, mhlpe.getYRot());

						event.getPoseStack().pushPose();

						Vec3 translate = mhlpe.position().subtract(le.position());
						event.getPoseStack().translate(translate.x(), translate.y(), translate.z());

						((EntityRenderer<MHLibPartEntity<?>>) renderer).render(mhlpe, f, event.getPartialTick(), event.getPoseStack(), event.getBufferSource(), event.getPackedLight());

						event.getPoseStack().popPose();
					} else {
						continue;
					}
				}
			}
		}
		
		for(GeoRenderLayer<?> gle : event.getRenderer().getRenderLayers()) {
			if(gle instanceof BoneInformationCollectorLayer<?> bicl) {
				bicl.onPostRender(event.getEntity());
			}
		}
	}

}
