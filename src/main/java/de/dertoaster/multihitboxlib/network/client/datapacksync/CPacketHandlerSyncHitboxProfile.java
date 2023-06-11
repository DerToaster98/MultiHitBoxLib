package de.dertoaster.multihitboxlib.network.client.datapacksync;

import java.util.Map;
import java.util.function.Supplier;

import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.network.AbstractPacketHandler;
import de.dertoaster.multihitboxlib.network.server.datapacksync.SPacketSyncHitboxProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class CPacketHandlerSyncHitboxProfile extends AbstractPacketHandler<SPacketSyncHitboxProfile> {

	@Override
	protected void execHandlePacket(SPacketSyncHitboxProfile packet, Supplier<Context> context, Level world, Player player) {
		for(Map.Entry<ResourceLocation, HitboxProfile> entry : packet.getData().entrySet()) {
			packet.getDatapackmanager().getData().putIfAbsent(entry.getKey(), entry.getValue());
		}
	}

}
