package de.dertoaster.multihitboxlib.client;

import de.dertoaster.multihitboxlib.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MHLibClient {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
	}

}
