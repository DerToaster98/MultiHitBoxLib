package de.dertoaster.multihitboxlib.assetsynch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.event.server.AssetEnforcementManagerRegistrationEvent;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataContainer;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataManagerData;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchEntryData;
import de.dertoaster.multihitboxlib.entity.hitbox.AssetEnforcementConfig;
import de.dertoaster.multihitboxlib.entity.hitbox.HitboxProfile;
import de.dertoaster.multihitboxlib.init.MHLibDatapackLoaders;
import de.dertoaster.multihitboxlib.init.MHLibPackets;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSynchAssets;
import de.dertoaster.multihitboxlib.util.CompressionUtil;
import de.dertoaster.multihitboxlib.util.LazyLoadField;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;

public class AssetEnforcement {
	
	private static final Map<ResourceLocation, AbstractAssetEnforcementManager> REGISTERED_MANAGERS = new Object2ObjectArrayMap<>();
	public static final LazyLoadField<Set<ResourceLocation>> ASSETS_TO_SYNCH = new LazyLoadField<>(AssetEnforcement::collectAssetsToSynch, 600000);
	
	public static void init() {
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
		final Set<ResourceLocation> assetsToSynch = ASSETS_TO_SYNCH.get();
		
		sendSynchData(connection, assetsToSynch);
	}
	
	public static void sendSynchData(final ServerPlayer connection, final Set<ResourceLocation> assetsToSynch) {
		sendSynchData(connection, assetsToSynch, false);
	}
	
	public static void sendSynchData(final ServerPlayer connection, final Set<ResourceLocation> assetsToSynch, boolean separatePackets) {
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
		return DistExecutor.safeRunForDist(() -> () -> {
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
						final byte[] decoded = Base64.getDecoder().decode(decompressed);
						
						// File saved, now load it, shall we?
						Boolean resultTmp = DistExecutor.safeCallWhenOn(Dist.CLIENT, () -> () -> manager.receiveAndLoad(data.id(), decoded));
						result &= resultTmp != null && resultTmp;
					} catch (IOException e) {
						e.printStackTrace();
					} catch (DataFormatException e) {
						e.printStackTrace();
					}
				}
			}
			
			Thread diskAccessThread = new Thread(runner);
			diskAccessThread.setDaemon(true);
			diskAccessThread.start();
			
			return result;
		}, () -> () -> false);
	}
	
	public static boolean handlePacketData(final SynchDataContainer payload) {
		boolean result = payload != null && !payload.payload().isEmpty();
		for (SynchDataManagerData entry : payload.payload()) {
			result &= handleEntry(entry);
		}
		return result;
	}
	
	private static final Predicate<ResourceLocation> RS_CHECK_PREDICATE = rs -> !rs.toString().isBlank() && !rs.getNamespace().isBlank() && !rs.getPath().isBlank();
	
	public static Set<ResourceLocation> collectAssetsToSynch() {
		Set<ResourceLocation> result = new HashSet<>();
		
		for (HitboxProfile hp : MHLibDatapackLoaders.HITBOX_PROFILES.getData().values()) {
			if (hp == null) {
				continue;
			}
			AssetEnforcementConfig aec = hp.assetConfig();
			if (aec == null) {
				continue;
			}
			result.addAll(aec.animations().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
			result.addAll(aec.models().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
			result.addAll(aec.textures().stream().filter(Objects::nonNull).filter(RS_CHECK_PREDICATE).collect(Collectors.toList()));
		}
		
		return result;
	}

}
