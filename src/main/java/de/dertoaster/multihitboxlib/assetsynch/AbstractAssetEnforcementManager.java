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

import com.google.common.primitives.Bytes;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

public abstract class AbstractAssetEnforcementManager<T extends Object> {

	private static final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> REGISTERED_MANAGERS = new Object2ObjectArrayMap<>();
	
	private final File directory = new File(Constants.MHLIB_ASSET_DIR, this.getSubDirectoryName());
	private final File syncDirectory = new File(Constants.MHLIB_SYNC_DIR, this.getSubDirectoryName());
	
	private ResourceLocation id = null; 

	public static void init() {
		final Map<ResourceLocation, AbstractAssetEnforcementManager<?>> map = new Object2ObjectArrayMap<>();
		AssetEnforcementManagerRegistrationEvent event = new AssetEnforcementManagerRegistrationEvent(map);
		Bus.MOD.bus().get().post(event);
		if (map != null) {
			map.entrySet().forEach(entry -> {
				try {
					registerEnforcementManager(entry.getKey(), entry.getValue());
				} catch (IOException e) {
					e.printStackTrace();
					map.remove(entry.getKey());
				}
			});
		}
	}

	protected static void registerEnforcementManager(ResourceLocation key, AbstractAssetEnforcementManager<?> value) throws IOException {
		if (key == null) {
			MHLibMod.LOGGER.error("Can not register asset enforcer with null key!");
			return;
		}
		try {
			if (!value.initDirectories()) {
				throw new IOException("Unable to create sync and asset directory for asset manager <" + key.toString() + ">!");
			}
			value.id = key;
			REGISTERED_MANAGERS.put(key, value);
		} catch (NullPointerException npe) {
			MHLibMod.LOGGER.error("Asset enforcement manager for id <" + key.toString() + "> could NOT be registered!");
		}
	}

	protected boolean initDirectories() {
		try {
			MHLibMod.checkAndCreateFolder(this.directory);
			MHLibMod.checkAndCreateFolder(this.syncDirectory);
		} catch (IOException ex) {
			return false;
		}
		return true;
	}
	
	public static boolean handleEntry(final SynchEntryData entry) {
		return DistExecutor.safeRunForDist(() -> () -> {
			if (!entry.validate()) {
				return false;
			}
			AbstractAssetEnforcementManager<?> manager = REGISTERED_MANAGERS.get(entry.manager());
			if (manager == null) {
				//TODO: Log
				return false;
			}
			final byte[] payload = entry.getPayLoadArray();
			if (manager.writeFile(entry.id(), payload)) {
				// File saved, now load it, shall we?
				return manager.receiveAndLoad(entry.id(), payload);
			}
			return false;
		}, () -> () -> false);
	}
	
	public Optional<SynchEntryData> createSynchEntry(final ResourceLocation id) {
		Optional<byte[]> optData = this.encodeData(id);
		if (!optData.isPresent()) {
			return Optional.empty();
		}
		byte[] data = optData.get();
		return Optional.of(new SynchEntryData(this.getId(), id, Bytes.asList(data)));
	}

	public final ResourceLocation getId() {
		return this.id;
	}

	protected abstract void registerAsset(ResourceLocation id, T asset);

	protected abstract Optional<byte[]> encodeData(final ResourceLocation id);
	
	protected abstract boolean receiveAndLoad(final ResourceLocation id, final byte[] data);
	
	public abstract String getSubDirectoryName();
	
	public abstract Optional<T> getAsset(final ResourceLocation id);
	
	protected File getSidedDirectory() {
		return DistExecutor.safeRunForDist(() -> () -> this.syncDirectory, () -> () -> this.directory);
	}

	protected boolean writeFile(final ResourceLocation id, final byte[] data) {
		final File destination = new File(this.getSidedDirectory(), id.getNamespace() + "/" + id.getPath());
		if (destination.exists() || destination.isDirectory()) {
			if (!destination.delete()) {
				//TODO: Throw exception and log
				return false;
			}
		}
		if (!decodeBase64ToFile(destination, data)) {
			//TODO: Log and throw exception
			return false;
		}
		return true;
	}
	
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
