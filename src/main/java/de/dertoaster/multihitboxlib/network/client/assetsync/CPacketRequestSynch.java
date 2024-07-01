package de.dertoaster.multihitboxlib.network.client.assetsync;

import de.dertoaster.multihitboxlib.api.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;

/*
 * TODO: Rewrite Packets for new Packet system
 */
public class CPacketRequestSynch extends AbstractPacket<CPacketRequestSynch> {

	@Override
	public Class<CPacketRequestSynch> getPacketClass() {
		return CPacketRequestSynch.class;
	}

	@Override
	public CPacketRequestSynch fromBytes(FriendlyByteBuf buffer) {
		return new CPacketRequestSynch();
	}

	@Override
	public void toBytes(CPacketRequestSynch packet, FriendlyByteBuf buffer) {
		
	}

}
