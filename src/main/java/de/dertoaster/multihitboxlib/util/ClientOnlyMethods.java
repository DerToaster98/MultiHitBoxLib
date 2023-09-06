package de.dertoaster.multihitboxlib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientOnlyMethods {

	public static Player getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	public static Level getWorld() {
		return Minecraft.getInstance().level;
	}

	public static boolean isCurrentPlayerOwnerIfIntegratedServer() {
		Player p = getClientPlayer();
		IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
		return p != null && integratedServer != null && integratedServer.isSingleplayerOwner(p.getGameProfile());
	}

}
