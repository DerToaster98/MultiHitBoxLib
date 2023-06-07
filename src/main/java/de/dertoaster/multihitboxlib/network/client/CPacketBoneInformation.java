package de.dertoaster.multihitboxlib.network.client;

import de.dertoaster.multihitboxlib.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;

public class CPacketBoneInformation extends AbstractPacket<CPacketBoneInformation> {

	@Override
	public Class<CPacketBoneInformation> getPacketClass() {
		return CPacketBoneInformation.class;
	}

	@Override
	public CPacketBoneInformation fromBytes(FriendlyByteBuf buffer) {
		return null;
	}

	@Override
	public void toBytes(CPacketBoneInformation packet, FriendlyByteBuf buffer) {
		
	}

}
