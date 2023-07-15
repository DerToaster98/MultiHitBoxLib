package de.dertoaster.multihitboxlib;

import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.FORGE)
public class GameEventHandler {

	@SubscribeEvent
	public static void onPlayerJoinServer(PlayerLoggedInEvent event) {
		// TODO: Collect everything that needs to be synched ONCE, then cache that away=> use lazyloadfield?
		// After that, create the packet and send it down to the client
	}
	
}
