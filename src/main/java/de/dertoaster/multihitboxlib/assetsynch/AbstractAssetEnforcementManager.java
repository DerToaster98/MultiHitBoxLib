package de.dertoaster.multihitboxlib.assetsynch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import javax.annotation.Nonnull;

import com.google.common.primitives.Bytes;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import de.dertoaster.multihitboxlib.util.LazyLoadField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;

public abstract class AbstractAssetEnforcementManager implements PackResources {

	private final File directory = this.createServerDirectory();
	private final File syncDirectory = this.createSynchDirectory();
	
	private final BuiltInMetadata builtInMetadata = this.createBuiltInMetaData();
	private final LazyLoadField<Set<String>> namespaces = new LazyLoadField<>(this::extractNamespaces);
	
	private final Set<ResourceLocation> CURRENTLY_ENFORCED_ASSETS = new HashSet<>();
	private final Set<String> extractNamespaces() {
		Set<String> result = new HashSet<>();
		this.CURRENTLY_ENFORCED_ASSETS.forEach(rs -> {
			result.add(rs.getNamespace());
		});
		return result;
	}
	
	private ResourceLocation id = null; 
	
	protected abstract Optional<byte[]> encodeData(final ResourceLocation id);
	
	protected abstract boolean receiveAndLoadInternally(final ResourceLocation id, final byte[] data);
	public abstract String getSubDirectoryName();
	
	protected final boolean receiveAndLoad(final ResourceLocation id, final byte[] data) {
		if (!this.CURRENTLY_ENFORCED_ASSETS.add(id)) {
			MHLibMod.LOGGER.warn("Asset with id <{}> is already loaded for enforcement manager <{}>!", id, this.getId());
		}
		return this.receiveAndLoadInternally(id, data);
	}
	
	final void setId(final ResourceLocation id) {
		if (this.id == null) {
			this.id = id;
		}
	}
	
	final void clearEnforcedAssetList() {
		this.CURRENTLY_ENFORCED_ASSETS.clear();
	}
	
	@Nonnull
	protected abstract File createServerDirectory();
	
	@Nonnull
	protected abstract File createSynchDirectory();

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
	
	public void reloadAll() {
		this.namespaces.reset();
		for (ResourceLocation id : this.CURRENTLY_ENFORCED_ASSETS) {
			File dataFile = this.getFileForId(id);
			if (dataFile == null) {
				continue;
			}
			if (!dataFile.exists()) {
				// TODO: Log
				continue;
			}
			if (!dataFile.isFile()) {
				// TODO: Log
				continue;
			}
			
			try {
				final byte[] bytes = Files.readAllBytes(dataFile.toPath());
				
				// now, load it
				if (!this.receiveAndLoadInternally(id, bytes)) {
					// TODO: Log
				}
			} catch (IOException e) {
				// TODO: Log
				e.printStackTrace();
				continue;
			}
		}
	}
	
	protected File getFileForId(final ResourceLocation id) {
		if (id == null) {
			return this.getSidedDirectory();
		}
		final File destination = new File(this.getSidedDirectory(), id.getNamespace() + "/" + id.getPath());
		return destination;
	}
	
	protected static boolean ensureFileFor(File destination, ResourceLocation id) {
		if (destination.exists() || destination.isDirectory()) {
			if (!destination.delete()) {
				//TODO: Throw exception and log
				return false;
			}
		}
		return true;
	}

	protected boolean writeFile(final ResourceLocation id, final byte[] data) {
		final File destination = this.getFileForId(id);
		if (!ensureFileFor(destination, id)) {
			return false;
		}
		if (!decodeAndWriteToFile(destination, data)) {
			//TODO: Log and throw exception
			return false;
		}
		return true;
	}
	
	public static byte[] encodeToBytes(Path path) {
		try {
			byte[] fileContent = Files.readAllBytes(path);
			return CompressionUtil.compress(fileContent, Deflater.BEST_COMPRESSION, true);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean decodeAndWriteToFile(String filePathWithNameAndExtension, byte[] base64) {
		return decodeAndWriteToFile(new File(filePathWithNameAndExtension), base64);
	}
	
	public static boolean decodeAndWriteToFile(File targetFile, byte[] compressedDearr) {
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
		return writeToFile(targetFile, dearr);
	}
	
	public static boolean writeToFile(File targetFile, byte[] bytes) {
		if (!targetFile.getParentFile().exists() || !targetFile.getParentFile().isDirectory()) {
			if (!targetFile.getParentFile().mkdirs()) {
				return false;
			}
		}
		
		try (FileOutputStream fos = new FileOutputStream(targetFile)) {
			fos.write(bytes);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public IoSupplier<InputStream> getRootResource(String... pElements) {
		return null;
	}
	
	@Override
	public IoSupplier<InputStream> getResource(PackType pPackType, ResourceLocation pLocation) {
		if (pPackType.equals(PackType.SERVER_DATA)) {
			return null;
		}
		File file = this.getFileForId(pLocation);
		if (file == null || !file.exists()) {
			return null;
		}
		return IoSupplier.create(file.toPath());
	}
	
	@Override
	public void listResources(PackType pPackType, String pNamespace, String pPath, ResourceOutput pResourceOutput) {
		if (pPackType.equals(PackType.SERVER_DATA)) {
			return;
		}
		if (!this.getNamespaces(pPackType).contains(pNamespace)) {
			return;
		}
		this.CURRENTLY_ENFORCED_ASSETS.forEach(rs -> {
			if (rs.getNamespace().equalsIgnoreCase(pNamespace) && rs.getPath().startsWith(pPath)) {
				pResourceOutput.accept(rs, this.getResource(pPackType, rs));
			}
		});
	}
	
	@Override
	public Set<String> getNamespaces(PackType pType) {
		if (pType.equals(PackType.SERVER_DATA)) {
			return Set.of();
		}
		return this.namespaces.get();
	}
	
	@Override
	public <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) {
		return this.builtInMetadata.get(pDeserializer);
	}
	
	@Override
	public String packId() {
		return this.getId().toString();
	}
	
	@Override
	public boolean isBuiltin() {
		return true;
	}
	
	@Override
	public void close() {
		
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
	private BuiltInMetadata createBuiltInMetaData() {
		return BuiltInMetadata.of();
	}

	public PackType packType() {
		return PackType.CLIENT_RESOURCES;
	}

}
