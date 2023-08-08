package de.dertoaster.multihitboxlib.assetsynch.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.zip.Deflater;

import de.dertoaster.multihitboxlib.assetsynch.client.TextureClientLogic;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;

public class TextureEnforcementManager extends MHLibEnforcementManager {
	
	private volatile Map<ResourceLocation, Pair> DESERIALIZED_PAIRS = new Object2ObjectArrayMap<>();
	
	public static class Pair implements Serializable {
		private static final long serialVersionUID = -7300641348899040116L;
		
		private final byte[] a;
		private final byte[] b;
		
		public Pair(byte[] a, byte[] b) {
			this.a = a;
			this.b = b;
		}
		
		public byte[] getA() {
			return a;
		}
		
		public byte[] getB() {
			return b;
		}
		
		public byte[] serialize() {
			int length = this.a.length;
			if (this.b != null) {
				length += this.b.length;
			}
			ByteBuf bb = Unpooled.buffer(length);
			boolean hasMeta = this.b != null && this.b.length > 0;
			bb.writeInt(this.a.length);
			bb.writeBytes(this.a);
			bb.writeBoolean(hasMeta);
			if (hasMeta) {
				bb.writeInt(this.b.length);
				bb.writeBytes(this.b);
			}
			
			return bb.array();
		}
		
		public static Pair deserialize(final byte[] bytes) {
			ByteBuf bb = Unpooled.copiedBuffer(bytes);
			int length = bb.readInt();
			// TODO: Throws error
			byte[] a = new byte[length];
			bb.readBytes(a);
			if (bb.readBoolean()) {
				length = bb.readInt();
				byte[] b = new byte[length];
				bb.readBytes(b);
				
				return new Pair(a, b);
			} else {
				return new Pair(a, new byte[] {});
			}
		}
	}
	
	@Override
	protected Optional<byte[]> encodeData(ResourceLocation id) {
		File location = this.getFileForId(id);
		if (!location.exists() || !location.isFile()) {
			return Optional.empty();
		}
		Optional<byte[]> opt = Optional.empty();
		try {
			opt = Optional.of(Files.readAllBytes(location.toPath()));
		} catch (IOException e1) {
			e1.printStackTrace();
			opt = Optional.empty();
		}
		if (opt.isEmpty()) {
			return opt;
		}
		byte[] texture = opt.get();
		File metaFile = this.getFileForId(id.withSuffix(".mcmeta"));
		byte[] result;
		if (metaFile.exists() && metaFile.isFile()) {
			Pair entry = new Pair(texture, encodeToBytes(metaFile.toPath()));
			result = entry.serialize();
			
		} else {
			Pair entry = new Pair(texture, new byte[] {});
			result = entry.serialize();
		}
		byte[] payload = null;
		try {
			payload = CompressionUtil.compress(result, Deflater.BEST_COMPRESSION, true);
		} catch(IOException e) {
			e.printStackTrace();
			payload = null;
		}
		return Optional.ofNullable(payload);
	}

	@Override
	protected boolean receiveAndLoadInternally(ResourceLocation id, byte[] data) {
		Pair pair = Pair.deserialize(data);
		DESERIALIZED_PAIRS.put(id, pair);
		if (pair == null) {
			return false;
		}
		byte[] texture = null;
		byte[] meta = null;
		if (pair.getA() != null && pair.getA().length > 0) {
			texture = pair.getA();
			if (pair.getB() != null && pair.getB().length > 0) {
				meta = pair.getB();
			}
		}
		if (texture == null && meta == null) {
			return false;
		}
		if (texture.length <= 0) {
			return false;
		}
		// Here goes nothing...
		try {
			return TextureClientLogic.receiveAndLoad(this, id, texture);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	protected boolean writeFile(ResourceLocation id, byte[] data) {
		Pair pair = DESERIALIZED_PAIRS.getOrDefault(id, null);
		if (pair == null) {
			pair = Pair.deserialize(data);
		}
		if (pair == null) {
			return false;
		}
		if (pair.getA() != null && pair.getA().length > 0) {
			File target = this.getFileForId(id);
			if (ensureFileFor(target, id) && writeToFile(target, pair.getA())) {
				if (pair.getB() != null && pair.getB().length > 0) {
					ResourceLocation metaId = id.withSuffix(".mcmeta");
					target = this.getFileForId(metaId);
					// TODO: Fix, doesn't create the right content in the file...
					return ensureFileFor(target, metaId) && writeToFile(target, pair.getB());
				} else {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getSubDirectoryName() {
		return "textures";
	}

}
