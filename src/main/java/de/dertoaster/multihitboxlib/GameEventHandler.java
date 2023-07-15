package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Constants.MODID, bus = Bus.FORGE)
public class GameEventHandler {

	@SubscribeEvent
	public static void onPlayerJoinServer(PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer sp && sp != null) {
			AssetEnforcement.sendSynchData(sp);
		}
	}
	
}
