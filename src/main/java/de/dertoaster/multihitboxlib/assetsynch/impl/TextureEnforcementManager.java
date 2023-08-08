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
		
		private final byte[] textureBytes;
		private final byte[] metaBytes;
		
		public Pair(byte[] textBytes, byte[] metaBytes) {
			this.textureBytes = textBytes;
			if(metaBytes == null) {
				this.metaBytes = new byte[] {};
			} else {
				this.metaBytes = metaBytes;
			}
		}
		
		public byte[] getA() {
			return textureBytes;
		}
		
		public byte[] getB() {
			return metaBytes;
		}
		
		public byte[] serialize() {
			int length = this.textureBytes.length;
			if (this.metaBytes != null) {
				length += this.metaBytes.length;
			}
			ByteBuf bb = Unpooled.buffer(length);
			bb.writeInt(this.textureBytes.length);
			bb.writeBytes(this.textureBytes);
			bb.writeInt(this.metaBytes.length);
			bb.writeBytes(this.metaBytes);
			
			return bb.array();
		}
		
		public static Pair deserialize(final byte[] bytes) {
			ByteBuf bb = Unpooled.copiedBuffer(bytes);
			int length = bb.readInt();
			byte[] a = new byte[length];
			bb.readBytes(a);
			length = bb.readInt();
			byte[] b = new byte[length];
			bb.readBytes(b);
			
			return new Pair(a, b);
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
			byte[] metaBytes = null;
			try {
				metaBytes = Files.readAllBytes(metaFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				metaBytes = new byte[] {};
			}
			Pair entry = new Pair(texture, metaBytes);
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
