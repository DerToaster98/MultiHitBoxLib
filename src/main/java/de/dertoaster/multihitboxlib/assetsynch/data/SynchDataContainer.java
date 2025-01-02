package de.dertoaster.multihitboxlib.assetsynch.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSyncDataContent;

// TODO: Create a method that creates a list of packets that can be sent to the client => Split into multiple blocks of 1MiB of data
public record SynchDataContainer(
		List<SynchDataManagerData> payload
) {
	
	public static final Codec<SynchDataContainer> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				SynchDataManagerData.CODEC.listOf().fieldOf("payload").forGetter(SynchDataContainer::payload)
			).apply(instance, SynchDataContainer::new);
	});

	public Set<SPacketSyncDataContent> getPacketList() {
		Set<SPacketSyncDataContent> packets = new HashSet<>();
		for (SynchDataManagerData data : this.payload) {
			packets.add(new SPacketSyncDataContent(data));
		}
		return packets;
	}

}
