package de.dertoaster.multihitboxlib.api.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractSPacketSyncDatapackContent<C extends Object, T extends AbstractSPacketSyncDatapackContent<C, ?>> implements IMHLibCustomPacketPayload<T> {

	protected final Codec<Map<ResourceLocation, C>> MAPPER = this.createMapper();
	public final Map<ResourceLocation, C> data;
	protected final Codec<C> CODEC = this.getCodec();
	
	protected abstract Codec<C> getCodec();
	protected abstract T createFromPacket(Map<ResourceLocation, C> data);
	public abstract BiConsumer<ResourceLocation, C> consumer();
	protected final StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC = buildStreamCodec();

	public AbstractSPacketSyncDatapackContent() {
		this.data = null;
	}
	
	protected StreamCodec<RegistryFriendlyByteBuf, T> buildStreamCodec() {
		return CustomPacketPayload.codec(AbstractSPacketSyncDatapackContent::write, this::read);
	}
	
	@Override
	public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
		return this.STREAM_CODEC;
	}
	
	public AbstractSPacketSyncDatapackContent(Map<ResourceLocation, C> data) {
		this.data = data;
	}
	
	public Map<ResourceLocation, C> getData() {
		return this.data;
	}
	
	public T read(RegistryFriendlyByteBuf buffer) {
		return this.createFromPacket(this.MAPPER.parse(NbtOps.INSTANCE, buffer.readNbt()).result().orElse(new HashMap<>()));
	}
	
	public void write(RegistryFriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (this.MAPPER.encodeStart(NbtOps.INSTANCE, this.data).result().orElse(new CompoundTag())));
	}
	
	protected Codec<Map<ResourceLocation, C>> createMapper() {
		return Codec.unboundedMap(ResourceLocation.CODEC, this.getCodec());
	}

	
}
