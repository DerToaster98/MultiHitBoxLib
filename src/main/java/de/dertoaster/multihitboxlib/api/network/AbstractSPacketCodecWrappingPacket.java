package de.dertoaster.multihitboxlib.api.network;

import java.io.IOException;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import de.dertoaster.multihitboxlib.util.CompressionUtil;
import net.minecraft.network.FriendlyByteBuf;

public abstract class AbstractSPacketCodecWrappingPacket<T extends Object, P extends AbstractSPacketCodecWrappingPacket<T, ?>> implements IMessage<P> {

	protected final T data;
	
	public AbstractSPacketCodecWrappingPacket() {
		this.data = null;
	}
	
	public AbstractSPacketCodecWrappingPacket(T data) {
		this.data = data;
	}
	
	protected abstract Codec<T> codec();
	protected abstract P createPacket(DataResult<T> dr);
	
	@Override
	public P fromBytes(FriendlyByteBuf buffer) {
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
				bytes = Base64.getDecoder().decode(bytes);
				JsonElement je = JsonParser.parseString(new String(bytes));
				DataResult<T> dr = this.codec().parse(JsonOps.INSTANCE, je);
				return this.createPacket(dr);
			}
		}
		return null;
	}

	@Override
	public void toBytes(P packet, FriendlyByteBuf buffer) {
		DataResult<JsonElement> dr = packet.codec().encodeStart(JsonOps.COMPRESSED, packet.data);
		JsonElement je = dr.getOrThrow(false, (s) -> {
			
		});
		if (je != null) {
			byte[] bytes = je.toString().getBytes();
			bytes = Base64.getEncoder().encode(bytes);
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
	}
	
	public T getData() {
		return this.data;
	}


}
