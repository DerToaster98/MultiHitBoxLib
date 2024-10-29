package de.dertoaster.multihitboxlib.assetsynch.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SynchEntryData(
		ResourceLocation id,
		List<Byte> payload
		) {
	
	public static final Codec<SynchEntryData> CODEC = RecordCodecBuilder.create(instance -> {
		return instance.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter(SynchEntryData::id),
				Codec.BYTE.listOf().fieldOf("payload").forGetter(SynchEntryData::payload)
			).apply(instance, SynchEntryData::new);
	});

	public boolean validate() {
		return this.id != null && !this.id.toString().isEmpty() && this.payload != null && !this.payload.isEmpty();
	}
	
	public byte[] getPayLoadArray() {
		byte[] result = new byte[this.payload.size()];
		for (int i = 0; i < this.payload.size(); i++) {
			result[i] = this.payload.get(i);
		}
		return result;
	}

}
