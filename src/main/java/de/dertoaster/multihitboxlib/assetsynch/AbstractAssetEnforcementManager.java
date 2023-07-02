package de.dertoaster.multihitboxlib.assetsynch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

public abstract class AbstractAssetEnforcementManager<T extends Object> {

	private static final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> REGISTERED_MANAGERS = new Object2ObjectArrayMap<>();

	public static void init() {
		final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> map = new Object2ObjectArrayMap<>();
		AssetEnforcementManagerRegistrationEvent event = new AssetEnforcementManagerRegistrationEvent(map);
		MinecraftForge.EVENT_BUS.post(event);
		if (map != null) {
			map.entrySet().forEach(entry -> registerEnforcementManager(entry.getKey(), entry.getValue()));
		}
	}

	protected static void registerEnforcementManager(ResourceLocation key, AbstractAssetEnforcementManager<?> value) {
		if (key == null) {
			MHLibMod.LOGGER.error("Can not register asset enforcer with null key!");
			return;
		}
		try {
			REGISTERED_MANAGERS.put(key, value);
		} catch (NullPointerException npe) {
			MHLibMod.LOGGER.error("Asset enforcement manager for id <" + key.toString() + "> could NOT be registered!");
		}
	}

	protected abstract void registerAsset(ResourceLocation id, T asset);

	protected abstract Optional<byte[]> findAsset(final ResourceLocation id);

	public static byte[] encodeFileToBase64(Path path) {
		try {
			byte[] fileContent = Files.readAllBytes(path);
			byte[] uncompressedResult = Base64.getEncoder().encode(fileContent);
			return compress(uncompressedResult, Deflater.BEST_COMPRESSION, true);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean decodeBase64ToFile(String filePathWithNameAndExtension, byte[] base64) {
		return decodeBase64ToFile(new File(filePathWithNameAndExtension), base64);
	}

	public static boolean decodeBase64ToFile(File targetFile, byte[] base64) {
		byte[] compressedDearr = Base64.getDecoder().decode(base64);
		byte[] dearr = new byte[0];
		try {
			dearr = decompress(compressedDearr, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (DataFormatException e1) {
			e1.printStackTrace();
			return false;
		}
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			fos.write(dearr);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Source: http://www.java2s.com/example/java-book/compressing-byte-arrays.html
	public static byte[] compress(byte[] input, int compressionLevel, boolean GZIPFormat) throws IOException {

		// Create a Deflater object to compress data
		Deflater compressor = new Deflater(compressionLevel, GZIPFormat);

		// Set the input for the compressor
		compressor.setInput(input);

		// Call the finish() method to indicate that we have
		// no more input for the compressor object
		compressor.finish();

		// Compress the data
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[1024];
		int readCount = 0;

		while (!compressor.finished()) {
			readCount = compressor.deflate(readBuffer);
			if (readCount > 0) {
				// Write compressed data to the output stream
				bao.write(readBuffer, 0, readCount);
			}
		}

		// End the compressor
		compressor.end();

		// Return the written bytes from output stream
		return bao.toByteArray();
	}

	// Source: http://www.java2s.com/example/java-book/compressing-byte-arrays.html
	public static byte[] decompress(byte[] input, boolean GZIPFormat) throws IOException, DataFormatException {
		// Create an Inflater object to compress the data
		Inflater decompressor = new Inflater(GZIPFormat);

		// Set the input for the decompressor
		decompressor.setInput(input);

		// Decompress data
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		byte[] readBuffer = new byte[1024];
		int readCount = 0;

		while (!decompressor.finished()) {
			readCount = decompressor.inflate(readBuffer);
			if (readCount > 0) {
				// Write the data to the output stream
				bao.write(readBuffer, 0, readCount);
			}
		}

		// End the decompressor
		decompressor.end();

		// Return the written bytes from the output stream
		return bao.toByteArray();
	}

}
