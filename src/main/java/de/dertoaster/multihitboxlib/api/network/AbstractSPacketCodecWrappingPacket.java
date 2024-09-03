package de.dertoaster.multihitboxlib.api.network;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import de.dertoaster.multihitboxlib.util.CompressionUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class AbstractSPacketCodecWrappingPacket<T extends Object, P extends AbstractSPacketCodecWrappingPacket<T, ?>> implements IMHLibCustomPacketPayload<P> {

	protected final T data;
	protected final StreamCodec<RegistryFriendlyByteBuf, P> STREAM_CODEC = buildStreamCodec();
	protected abstract Codec<T> codec();
	protected abstract P createPacket(DataResult<T> dr);
	protected abstract P createPacket(T data);
	
	public AbstractSPacketCodecWrappingPacket() {
		this.data = null;
	}
	
	public AbstractSPacketCodecWrappingPacket(T data) {
		this.data = data;
	}
	
	protected StreamCodec<RegistryFriendlyByteBuf, P> buildStreamCodec() {
		return CustomPacketPayload.codec(AbstractSPacketCodecWrappingPacket::write, this::read);
	}
	
	@Override
	public StreamCodec<RegistryFriendlyByteBuf, P> getStreamCodec() {
		return STREAM_CODEC;
	}

	public P read(RegistryFriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			byte[] bytes = buffer.readByteArray();
			if (bytes.length > 0) {
				try {
					bytes = CompressionUtil.decompress(bytes, true);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} catch (DataFormatException e) {
					e.printStackTrace();
					return null;
				}
				JsonElement je = JsonParser.parseString(new String(bytes));
				DataResult<T> dr = this.codec().parse(JsonOps.COMPRESSED, je);
				return this.createPacket(dr);
			}
		}
		return this.createPacket((T)null);
		// Crashes the client for some odd reason
		//T data = buffer.readJsonWithCodec(this.codec());
		//return this.createPacket(data);
	}

	public void write(FriendlyByteBuf buffer) {
		DataResult<JsonElement> dr = this.codec().encodeStart(JsonOps.COMPRESSED, this.data);
		JsonElement je = dr.getOrThrow();
		if (je != null) {
			byte[] bytes = je.toString().getBytes();
			try {
				bytes = CompressionUtil.compress(bytes, Deflater.BEST_COMPRESSION, true);
				buffer.writeBoolean(true);
				buffer.writeByteArray(bytes);
			} catch (IOException e) {
				buffer.writeBoolean(false);
				e.printStackTrace();
			}
		} else {
			buffer.writeBoolean(false);
		}
		//buffer.writeJsonWithCodec(this.codec(), packet.getData());
	}
	
	public T getData() {
		return this.data;
	}


}
