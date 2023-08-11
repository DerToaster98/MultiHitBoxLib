package de.dertoaster.multihitboxlib.assetsynch;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.Deflater;

import de.dertoaster.multihitboxlib.assetsynch.impl.MHLibEnforcementManager;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractNInOneEntriesEnforcementManager extends MHLibEnforcementManager {
	
	private volatile Map<ResourceLocation, ByteEntryContainer> DESERIALIZED_PAIRS = new Object2ObjectArrayMap<>();
	
	public static class ByteEntryContainer extends ArrayList<byte[]> implements Serializable {
		private static final long serialVersionUID = -7300641348899040116L;
		
		public ByteEntryContainer() {
			super(1);
		}
		
		public ByteEntryContainer(int slots) {
			super(slots);
		}
		
		public byte[] serialize() {
			int length = 0;
			for (byte[] entry : this) {
				length += entry.length;
			}
			ByteBuf bb = Unpooled.buffer(length);
			bb.writeInt(this.size());
			this.forEach(entry -> {
				bb.writeInt(entry.length);
				bb.writeBytes(entry);
			});
			
			return bb.array();
		}
		
		public static ByteEntryContainer deserialize(final byte[] bytes) {
			ByteBuf bb = Unpooled.copiedBuffer(bytes);
			int length = bb.readInt();
			ByteEntryContainer result = new ByteEntryContainer(length);
			for (int i = 0; i < length; i++) {
				byte[] entry = new byte[bb.readInt()];
				bb.readBytes(entry);
				result.add(entry);
			}
			return result;
		}
	}
	
	protected abstract List<byte[]> getRawByteEntriesFor(ResourceLocation id);
	
	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		List<byte[]> relevantEntries = this.getRawByteEntriesFor(id);
		ByteEntryContainer container = new ByteEntryContainer();
		if (relevantEntries.isEmpty()) {
			return Optional.empty();
		}
		container.addAll(relevantEntries);
		byte[] result = container.serialize();
		byte[] payload = null;
		try {
			payload = CompressionUtil.compress(result, Deflater.BEST_COMPRESSION, true);
		} catch(IOException e) {
			e.printStackTrace();
			payload = null;
		}
		return Optional.ofNullable(payload);
	}
	
	protected abstract boolean loadEntry(final ResourceLocation id, byte[] data, int index);

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		ByteEntryContainer container = ByteEntryContainer.deserialize(data);
		DESERIALIZED_PAIRS.put(id, container);
		if (container == null || container.size() <= 0) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < container.size(); i++) {
			result &= this.loadEntry(id, container.get(i), i);
		}
		return result;
	}
	
	protected abstract boolean writeEntryToFile(final ResourceLocation id, byte[] data, int index);
	
	@Override
	protected boolean writeFile(ResourceLocation id, byte[] data) {
		ByteEntryContainer container = DESERIALIZED_PAIRS.getOrDefault(id, null);
		if (container == null) {
			container = ByteEntryContainer.deserialize(data);
		}
		if (container == null) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < container.size(); i++) {
			result &= this.writeEntryToFile(id, container.get(i), i);
		}
		
		return result;
	}

	@Override
	public String getSubDirectoryName() {
		return "textures";
	}

}
