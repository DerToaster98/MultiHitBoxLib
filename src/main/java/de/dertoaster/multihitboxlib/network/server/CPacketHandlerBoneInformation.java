package de.dertoaster.multihitboxlib.network.server;

import java.util.UUID;

import de.dertoaster.multihitboxlib.api.IMultipartEntity;
import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketHandler;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.ClientPayloadContext;
import net.neoforged.neoforge.network.handling.ServerPayloadContext;

public class CPacketHandlerBoneInformation implements IMHLibCustomPacketHandler<CPacketBoneInformation> {

	@Override
	public void handleClient(CPacketBoneInformation data, ClientPayloadContext context) {

	}

	@Override
	public void handleServer(CPacketBoneInformation data, ServerPayloadContext context) {
		context.enqueueWork(() -> {
			final Player player = context.player();
			if (player == null) {
				return;
			}
			final Level world = player.level();
			if (world == null) {
				return;
			}
			if (!(world instanceof ServerLevel && player instanceof ServerPlayer)) {
				// Illegal side, ignore => Somehow... idk if this can actually happen
				return;
			}
			final UUID senderID = player.getUUID();
			final int entityID = data.entityID();

			final Entity entity = world.getEntity(entityID);
			if(entity != null && entity instanceof IMultipartEntity<?> imp) {
				// Entity does not want synching, so we won't sync any data to it
				if(!imp.syncWithModel()) {
					return;
				}
				if(!senderID.equals(imp.getMasterUUID())) {
					// UUIDs do not match => warn
					return;
				}
				imp.processBoneInformation(data.boneInformation());
			}
		}).exceptionally(e -> {
			context.disconnect(Component.translatable("mhlib.networking.c2s_bone_info_failed", e.getMessage()));
			return null;
		});
	}
}
