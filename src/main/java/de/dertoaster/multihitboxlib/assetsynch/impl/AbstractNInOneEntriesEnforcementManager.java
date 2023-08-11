package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.Deflater;

import javax.annotation.Nullable;

import de.dertoaster.multihitboxlib.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractNInOneEntriesEnforcementManager extends MHLibEnforcementManager {
	
	private volatile Map<ResourceLocation, ByteEntryContainer> DESERIALIZED_PAIRS = new Object2ObjectArrayMap<>();
	
	public static class ByteEntryContainer implements Serializable {
		private static final long serialVersionUID = -7300641348899040116L;
		
		private final List<byte[]> entries;
		
		public ByteEntryContainer(byte[]... entries) {
			this(List.of(entries));
		}
		
		public ByteEntryContainer(List<byte[]> entries) {
			this.entries = entries;
		}
		
		public int getSize() {
			return this.entries != null ? this.entries.size() : 0;
		}
		
		@Nullable
		public byte[] getFirst() {
			return this.getAt(0);
		}
		
		@Nullable
		public byte[] getAt(int i) {
			return this.entries.size() > i ? this.entries.get(i) : null;
		}
		
		public byte[] serialize() {
			int length = 0;
			for (byte[] entry : this.entries) {
				length += entry.length;
			}
			ByteBuf bb = Unpooled.buffer(length);
			bb.writeInt(this.entries.size());
			this.entries.forEach(entry -> {
				bb.writeInt(entry.length);
				bb.writeBytes(entry);
			});
			
			return bb.array();
		}
		
		public static ByteEntryContainer deserialize(final byte[] bytes) {
			ByteBuf bb = Unpooled.copiedBuffer(bytes);
			int length = bb.readInt();
			List<byte[]> list = new ArrayList<>(length);
			for (int i = 0; i < length; i++) {
				byte[] entry = new byte[bb.readInt()];
				bb.readBytes(entry);
				list.add(entry);
			}
			return new ByteEntryContainer(list);
		}
	}
	
	protected abstract List<byte[]> getRawByteEntriesFor(ResourceLocation id);
	
	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		List<byte[]> relevantEntries = this.getRawByteEntriesFor(id);
		if (relevantEntries.isEmpty()) {
			return Optional.empty();
		}
		ByteEntryContainer container = new ByteEntryContainer(relevantEntries);
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
		if (container == null || container.getSize() <= 0) {
			return false;
		}
		boolean result = true;
		for (int i = 0; i < container.getSize(); i++) {
			result &= this.loadEntry(id, container.getAt(i), i);
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
		for (int i = 0; i < container.getSize(); i++) {
			result &= this.writeEntryToFile(id, container.getAt(i), i);
		}
		
		return result;
	}

	@Override
	public String getSubDirectoryName() {
		return "textures";
	}

}
