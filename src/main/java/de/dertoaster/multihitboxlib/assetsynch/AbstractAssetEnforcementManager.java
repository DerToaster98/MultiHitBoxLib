package de.dertoaster.multihitboxlib.assetsynch;

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

import com.google.common.primitives.Bytes;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataManagerData;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
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
	
	public static boolean handleEntry(final SynchDataManagerData entry) {
		return DistExecutor.safeRunForDist(() -> () -> {
			AbstractAssetEnforcementManager<?> manager = REGISTERED_MANAGERS.get(entry.manager());
			if (manager == null) {
				//TODO: Log
				return false;
			}
			for (SynchEntryData data : entry.payload()) {
				final byte[] payload = data.getPayLoadArray();
				if (manager.writeFile(data.id(), payload)) {
					// File saved, now load it, shall we?
					return manager.receiveAndLoad(data.id(), payload);
				}
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
		return Optional.of(new SynchEntryData(id, Bytes.asList(data)));
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
			return CompressionUtil.compress(uncompressedResult, Deflater.BEST_COMPRESSION, true);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean decodeBase64ToFile(String filePathWithNameAndExtension, byte[] base64) {
		return decodeBase64ToFile(new File(filePathWithNameAndExtension), base64);
	}

	public static boolean decodeBase64ToFile(File targetFile, byte[] compressedDearr) {
		byte[] dearr = new byte[0];
		try {
			dearr = CompressionUtil.decompress(compressedDearr, true);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (DataFormatException e1) {
			e1.printStackTrace();
			return false;
		}
		byte[] base64 = Base64.getDecoder().decode(dearr);
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			fos.write(base64);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
