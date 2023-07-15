package de.dertoaster.multihitboxlib.api.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

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
	protected abstract P createPacket(T data);
	
	@Override
	public P fromBytes(FriendlyByteBuf buffer) {
		// Crashes the client for some odd reason
		/*if (buffer.readBoolean()) {
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
		}*/
		T data = buffer.readJsonWithCodec(this.codec());
		return this.createPacket(data);
	}

	@Override
	public void toBytes(P packet, FriendlyByteBuf buffer) {
		/*DataResult<JsonElement> dr = packet.codec().encodeStart(JsonOps.COMPRESSED, packet.data);
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
		}*/
		buffer.writeJsonWithCodec(this.codec(), packet.getData());
	}
	
	public T getData() {
		return this.data;
	}


}
