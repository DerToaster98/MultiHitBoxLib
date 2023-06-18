package de.dertoaster.multihitboxlib.client;

import com.mojang.blaze3d.vertex.PoseStack;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.client.geckolib.renderlayer.BoneInformationCollectorLayer;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.mixin.accessor.AccessorEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.FORGE)
public class GeckolibEventHandler {
	
	@SubscribeEvent
	public static void onPostRenderEntity(GeoRenderEvent.Entity.Post event) {
		Entity animatable = event.getEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), event.getRenderer(), animatable);
	}
	
	@SubscribeEvent
	public static void onPostRenderReplacedEntity(GeoRenderEvent.ReplacedEntity.Post event) {
		Entity animatable = event.getReplacedEntity();
		performCommonLogic(event.getPoseStack(), event.getRenderer(), event.getBufferSource(), event.getPartialTick(), event.getPackedLight(), event.getRenderer(), animatable);
	}

	private static void performCommonLogic(PoseStack poseStack, EntityRenderer<?> entityRenderer, MultiBufferSource bufferSource, float partialTick, int packedLight, GeoRenderer<?> geoRenderer, Entity animatable) {
		if (!(animatable instanceof LivingEntity le)) {
			return;
		}
		if (le.isMultipartEntity() &&  animatable instanceof IMultipartEntity<?> ime && le.getParts() != null && le.getParts().length > 0) {
			for(PartEntity<?> part : le.getParts()) {
				if(part instanceof MHLibPartEntity<?> mhlpe) {
					if (mhlpe.hasCustomRenderer()) {
						EntityRenderer<? extends MHLibPartEntity<? extends Entity>> renderer = MHLibClient.getRendererFor(mhlpe, ((AccessorEntityRenderer)entityRenderer).getEntityRenderDispatcher());
						if (renderer == null) {
							continue;
						}

						float f = Mth.lerp(partialTick, mhlpe.yRotO, mhlpe.getYRot());

						poseStack.pushPose();

						Vec3 translate = mhlpe.position().subtract(le.position());
						poseStack.translate(translate.x(), translate.y(), translate.z());

						((EntityRenderer<MHLibPartEntity<?>>) renderer).render(mhlpe, f, partialTick, poseStack, bufferSource, packedLight);

						poseStack.popPose();
					} else {
						continue;
					}
				}
			}
		}
		
		for(GeoRenderLayer<?> gle : geoRenderer.getRenderLayers()) {
			if(gle instanceof BoneInformationCollectorLayer<?> bicl) {
				bicl.onPostRender(animatable);
			}
		}
	}

}
