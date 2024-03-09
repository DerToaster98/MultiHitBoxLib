package de.dertoaster.multihitboxlib.assetsynch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.zip.DataFormatException;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import de.dertoaster.multihitboxlib.api.event.server.SynchAssetFinderRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.assetfinders.AbstractAssetFinder;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataManagerData;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSynchAssets;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import de.dertoaster.multihitboxlib.util.LazyLoadFieldFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.PacketDistributor;

public class AssetEnforcement {
	
	private static volatile Map<ResourceLocation, AbstractAssetEnforcementManager> REGISTERED_MANAGERS = new Object2ObjectArrayMap<>();
	private static final Map<ResourceLocation, AbstractAssetFinder> REGISTERED_SYNCH_ASSET_FINDER = new Object2ObjectArrayMap<>();
	
	public static final LazyLoadFieldFunction<RegistryAccess, Set<ResourceLocation>> ASSETS_TO_SYNCH = new LazyLoadFieldFunction<>(AssetEnforcement::collectAssetsToSynch, 600000);
	
	public static void init() {
		initializeManagers();
		initializeAssetFinders();
	}
	
	public static Set<AbstractAssetEnforcementManager> getRegisteredManagers() {
		Set<AbstractAssetEnforcementManager> result = new HashSet<>();
		REGISTERED_MANAGERS.values().forEach(result::add);
		return result;
	}
	
	private static void initializeAssetFinders() {
		final Map<ResourceLocation, AbstractAssetFinder> map = new Object2ObjectArrayMap<>();
		SynchAssetFinderRegistrationEvent event = new SynchAssetFinderRegistrationEvent(map);
		Bus.MOD.bus().get().post(event);
		if (map != null) {
			map.entrySet().forEach(entry -> {
				registerAssetFinder(entry.getKey(), entry.getValue());
			});
		}
	}

	protected static void registerAssetFinder(ResourceLocation key, AbstractAssetFinder value) {
		if (key == null) {
			MHLibMod.LOGGER.error("Can not register asset finder with null key!");
			return;
		}
		try {
			REGISTERED_SYNCH_ASSET_FINDER.put(key, value);
		} catch (NullPointerException npe) {
			MHLibMod.LOGGER.error("Asset finder for id <" + key.toString() + "> could NOT be registered!");
		}
	}

	private static void initializeManagers() {
		final Map<ResourceLocation, AbstractAssetEnforcementManager> map = new Object2ObjectArrayMap<>();
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

	protected static void registerEnforcementManager(ResourceLocation key, AbstractAssetEnforcementManager value) throws IOException {
		if (key == null) {
			MHLibMod.LOGGER.error("Can not register asset enforcer with null key!");
			return;
		}
		try {
			if (!value.initDirectories()) {
				throw new IOException("Unable to create sync and asset directory for asset manager <" + key.toString() + ">!");
			}
			value.setId(key);
			REGISTERED_MANAGERS.put(key, value);
		} catch (NullPointerException npe) {
			MHLibMod.LOGGER.error("Asset enforcement manager for id <" + key.toString() + "> could NOT be registered!");
		}
	}
	
	public static void sendSynchData(final ServerPlayer connection) {
		// Collect everything that needs to be synched ONCE, then cache that away=> use lazyloadfield?
		// After that, create the packet and send it down to the client
		if (connection.level() == null || connection.level().registryAccess() == null) {
			MHLibMod.LOGGER.warn("No registry access for sending synch data on ServerPlayer (level or level.registryAccess is null)!");
			return;
		}
		final Set<ResourceLocation> assetsToSynch = ASSETS_TO_SYNCH.apply(connection.level().registryAccess());
		
		sendSynchData(connection, assetsToSynch);
	}
	
	public static void sendSynchData(final ServerPlayer connection, final Set<ResourceLocation> assetsToSynch) {
		sendSynchData(connection, assetsToSynch, false);
	}
	
	public static void sendSynchData(final ServerPlayer connection, final Set<ResourceLocation> assetsToSynch, boolean separatePackets) {
		if (assetsToSynch.isEmpty()) {
			// No need to do anything
			return;
		}
		
		List<SynchDataManagerData> managerData = new ArrayList<>();
		for (Map.Entry<ResourceLocation, AbstractAssetEnforcementManager> entry : REGISTERED_MANAGERS.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}
			List<SynchEntryData> content = new ArrayList<>();
			for (ResourceLocation id : assetsToSynch) {
				Optional<SynchEntryData> optSynchData = entry.getValue().createSynchEntry(id);
				optSynchData.ifPresent(content::add);
			}
			if (!content.isEmpty()) {
				managerData.add(new SynchDataManagerData(entry.getKey(), content));
				if (separatePackets) {
					sendPacket(connection, new SynchDataContainer(managerData));
					managerData.clear();
				}
			}
		}
		if (!managerData.isEmpty()) {
			sendPacket(connection, new SynchDataContainer(managerData));
		}
	}
	
	private static void sendPacket(final ServerPlayer connection, final SynchDataContainer payload) {
		SPacketSynchAssets packet = new SPacketSynchAssets(payload);
		MHLibPackets.send(packet, PacketDistributor.PLAYER.with(() -> connection));
	}
	
	public static boolean handleEntry(final SynchDataManagerData entry) {
		if (FMLEnvironment.dist.isClient()) {
			final AbstractAssetEnforcementManager manager = REGISTERED_MANAGERS.get(entry.manager());
			if (manager == null) {
				//TODO: Log
				return false;
			}
			final DiskSaveRunner runner = new DiskSaveRunner(manager, true);
			boolean result = true;
			for (SynchEntryData data : entry.payload()) {
				final byte[] payload = data.getPayLoadArray();
				if (runner.add(new Tuple<>(data.id(), payload))) {
					try {
						final byte[] decompressed = CompressionUtil.decompress(payload, true);
						
						// File saved, now load it, shall we?
						result &= manager.receiveAndLoad(data.id(), decompressed);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
			}
			
			Thread diskAccessThread = new Thread(runner);
			diskAccessThread.setName("MHLib - Asset-Sync writer thread");
			diskAccessThread.setDaemon(true);
			diskAccessThread.start();
			
			return result;
		}
		return false;
	}
	
	public static boolean handlePacketData(final SynchDataContainer payload) {
		boolean result = payload != null && !payload.payload().isEmpty();
		for (SynchDataManagerData entry : payload.payload()) {
			result &= handleEntry(entry);
		}
		
		return result;
	}
	
	public static Set<ResourceLocation> collectAssetsToSynch(RegistryAccess registryAccess) {
		Set<ResourceLocation> result = new HashSet<>();
		
		for(AbstractAssetFinder finder : REGISTERED_SYNCH_ASSET_FINDER.values()) {
			Set<ResourceLocation> supplied = finder.get(registryAccess);
			if (supplied == null || supplied.isEmpty()) {
				MHLibMod.LOGGER.warn("Asset finder with id <" + finder.getId() + "> returned null or an empty set! Skipping...");
				continue;
			}
			result.addAll(supplied);
		}
		
		return result;
	}
	
}
