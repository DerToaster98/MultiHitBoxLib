package de.dertoaster.multihitboxlib.assetsynch.data;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;

public record SynchDataManagerData(
		ResourceLocation manager,
		List<SynchEntryData> payload
		) {
	
	public static final Codec<SynchDataManagerData> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				ResourceLocation.CODEC.fieldOf("manager").forGetter(SynchDataManagerData::manager),
				SynchEntryData.CODEC.listOf().fieldOf("payload").forGetter(SynchDataManagerData::payload)
			).apply(instance, SynchDataManagerData::new);
	});

}
