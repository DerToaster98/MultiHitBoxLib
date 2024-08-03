package de.dertoaster.multihitboxlib.assetsynch.pack;

import java.util.function.Consumer;

import de.dertoaster.multihitboxlib.assetsynch.AbstractAssetEnforcementManager;
import de.dertoaster.multihitboxlib.assetsynch.AssetEnforcement;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.Pack.Position;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

public class AssetSynchPackFinder implements RepositorySource {
	
	public static final PackSource PACK_SOURCE = PackSource.create(name -> name.copy().withStyle(ChatFormatting.WHITE).append(" (Server-Enforced)").withStyle(ChatFormatting.GOLD), true);
	
	@Override
	public void loadPacks(Consumer<Pack> pOnLoad) {
		for (AbstractAssetEnforcementManager aaem : AssetEnforcement.getRegisteredManagers()) {
			Pack pack = Pack.create(
					aaem.getId().toString(), 
					Component.literal(aaem.getId().toString() + "(enforced assets)"), 
					true, 
					(s) -> aaem, 
					new Pack.Info(Component.literal("Enforced assets for manager: " + aaem.getId().toString()), SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES), FeatureFlagSet.of()), 
					aaem.packType(), 
					Position.TOP, 
					true, 
					PACK_SOURCE
			);
			
			if (pack != null) {
				pOnLoad.accept(pack);
			}
		}
	}
	
	private static final RepositorySource INSTANCE = new AssetSynchPackFinder();

	public static RepositorySource instance() {
		return INSTANCE;
	}

}
