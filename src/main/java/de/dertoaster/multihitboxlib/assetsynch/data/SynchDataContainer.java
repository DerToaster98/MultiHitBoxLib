package de.dertoaster.multihitboxlib.assetsynch.data;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SynchDataContainer(
		List<SynchDataManagerData> payload
		) {
	
	public static final Codec<SynchDataContainer> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				SynchDataManagerData.CODEC.listOf().fieldOf("payload").forGetter(SynchDataContainer::payload)
			).apply(instance, SynchDataContainer::new);
	});

}
