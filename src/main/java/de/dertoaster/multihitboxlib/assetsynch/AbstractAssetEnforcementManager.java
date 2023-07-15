package de.dertoaster.multihitboxlib.assetsynch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import com.google.common.primitives.Bytes;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public abstract class AbstractAssetEnforcementManager {

	private final File directory = new File(Constants.MHLIB_ASSET_DIR, this.getSubDirectoryName());
	private final File syncDirectory = new File(Constants.MHLIB_SYNC_DIR, this.getSubDirectoryName());
	
	private ResourceLocation id = null; 
	
	protected abstract Optional<byte[]> encodeData(final ResourceLocation id);
	protected abstract boolean receiveAndLoad(final ResourceLocation id, final byte[] data);
	public abstract String getSubDirectoryName();
	
	void setId(final ResourceLocation id) {
		if (this.id == null) {
			this.id = id;
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

	protected File getSidedDirectory() {
		if (FMLLoader.getDist().isDedicatedServer()) {
			return this.directory;
		} else {
			// Check for logical server
			if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
				return this.directory;
			}
			return this.syncDirectory;
		}
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
		if (!targetFile.getParentFile().mkdirs()) {
			return false;
		}
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
