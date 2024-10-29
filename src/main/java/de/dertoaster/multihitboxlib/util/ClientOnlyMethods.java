package de.dertoaster.multihitboxlib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientOnlyMethods {

    @Environment(EnvType.CLIENT)
    public static Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Environment(EnvType.CLIENT)
    public static Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Environment(EnvType.CLIENT)
    public static boolean isCurrentPlayerOwnerIfIntegratedServer() {
        Player p = getClientPlayer();
        IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();
        return p != null && integratedServer != null && integratedServer.isSingleplayerOwner(p.getGameProfile());
    }

}