package de.dertoaster.multihitboxlib.api.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractSPacketSyncDatapackContent<C extends Object, T extends AbstractSPacketSyncDatapackContent<C, ?>> implements IMessage<T> {

	protected final Codec<Map<ResourceLocation, C>> MAPPER = this.createMapper();
	public final Map<ResourceLocation, C> data;
	protected final Codec<C> CODEC = this.getCodec();
	
	protected abstract Codec<Map<ResourceLocation, C>> createMapper();
	protected abstract Codec<C> getCodec();
	protected abstract T createFromPacket(Map<ResourceLocation, C> data);
	public abstract BiConsumer<ResourceLocation, C> consumer();

	public AbstractSPacketSyncDatapackContent() {
		this.data = null;
	}
	
	public AbstractSPacketSyncDatapackContent(Map<ResourceLocation, C> data) {
		this.data = data;
	}
	
	public Map<ResourceLocation, C> getData() {
		return this.data;
	}
	
	@Override
	public T fromBytes(FriendlyByteBuf buffer) {
		return this.createFromPacket(this.MAPPER.parse(NbtOps.INSTANCE, buffer.readNbt()).result().orElse(new HashMap<>()));
	}
	
	@Override
	public void toBytes(T packet, FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (packet.MAPPER.encodeStart(NbtOps.INSTANCE, packet.data).result().orElse(new CompoundTag())));
	}

	
}
