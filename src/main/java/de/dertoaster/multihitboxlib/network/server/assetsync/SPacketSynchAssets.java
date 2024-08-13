package de.dertoaster.multihitboxlib.network.server.assetsync;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import de.dertoaster.multihitboxlib.api.network.AbstractSPacketCodecWrappingPacket;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;

// TODO: Make sure this is at most 1 MiB in size. If it is larger, we need to send multiple packets!
public class SPacketSynchAssets extends AbstractSPacketCodecWrappingPacket<SynchDataContainer, SPacketSynchAssets> {
	
	public SPacketSynchAssets() {
		super();
	}
	
	public SPacketSynchAssets(SynchDataContainer data) {
		super(data);
	}

	@Override
	public Class<SPacketSynchAssets> getPacketClass() {
		return SPacketSynchAssets.class;
	}

	@Override
	protected Codec<SynchDataContainer> codec() {
		return SynchDataContainer.CODEC;
	}

	@Override
	protected SPacketSynchAssets createPacket(DataResult<SynchDataContainer> dr) {
		SynchDataContainer sdc = dr.getOrThrow(false, (s) -> {
			//TODO
			
		});
		if (sdc != null) {
			return new SPacketSynchAssets(sdc);
		}
		return null;
	}

	@Override
	protected SPacketSynchAssets createPacket(SynchDataContainer data) {
		return new SPacketSynchAssets(data);
	}

}
