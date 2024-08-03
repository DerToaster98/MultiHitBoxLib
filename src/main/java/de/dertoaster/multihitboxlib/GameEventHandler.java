package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = Constants.MODID, bus = EventBusSubscriber.Bus.MOD)
public class GameEventHandler {

	@SubscribeEvent
	public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp && sp != null) {
			AssetEnforcement.sendSynchData(sp);
		}
	}
	
}
