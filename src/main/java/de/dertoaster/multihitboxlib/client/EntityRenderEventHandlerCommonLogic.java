package de.dertoaster.multihitboxlib.client;

import com.mojang.blaze3d.vertex.PoseStack;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.client.azurelib.AzurelibEntityRenderEventHandler;
import de.dertoaster.multihitboxlib.client.geckolib.GeckolibEntityRenderEventHandler;
import de.dertoaster.multihitboxlib.entity.MHLibPartEntity;
import de.dertoaster.multihitboxlib.mixin.accessor.AccessorEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.eventbus.api.IEventBus;

public abstract class EntityRenderEventHandlerCommonLogic {
	
	public static void registerRelevantEventListeners(final IEventBus bus) {
		if (Constants.Dependencies.isModLoaded(Constants.Dependencies.GECKOLIB_MODID)) {
			bus.addListener(GeckolibEntityRenderEventHandler::onPostRenderEntity);
			bus.addListener(GeckolibEntityRenderEventHandler::onPostRenderReplacedEntity);
		}
		if (Constants.Dependencies.isModLoaded(Constants.Dependencies.AZURELIB_MODID)) {
			bus.addListener(AzurelibEntityRenderEventHandler::onPostRenderEntity);
			bus.addListener(AzurelibEntityRenderEventHandler::onPostRenderReplacedEntity);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static void performCommonLogic(PoseStack poseStack, EntityRenderer<?> entityRenderer, MultiBufferSource bufferSource, float partialTick, int packedLight, Entity entitybeingRenderer) {
		if (!(entitybeingRenderer instanceof LivingEntity le)) {
			return;
		}
		if (le.isMultipartEntity() &&  entitybeingRenderer instanceof IMultipartEntity<?> ime && le.getParts() != null && le.getParts().length > 0) {
			for(PartEntity<?> part : le.getParts()) {
				if(part instanceof MHLibPartEntity<?> mhlpe) {
					if (mhlpe.hasCustomRenderer() && mhlpe.isPartEnabled()) {
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
	}

}
