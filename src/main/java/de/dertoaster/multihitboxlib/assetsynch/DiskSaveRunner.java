package de.dertoaster.multihitboxlib.assetsynch;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class DiskSaveRunner extends HashSet<Tuple<ResourceLocation, byte[]>> implements Runnable, Set<Tuple<ResourceLocation, byte[]>> {
	
	private static final long serialVersionUID = 6685706771120439838L;
	
	private final AbstractAssetEnforcementManager manager;
	private final boolean deleteDirectory;
	
	public DiskSaveRunner(final AbstractAssetEnforcementManager managerIn, boolean deleteBeforeSaving) {
		this.manager = managerIn;
		this.deleteDirectory = deleteBeforeSaving;
	}

	@Override
	public void run() {
		if (this.deleteDirectory) {
			if (FMLEnvironment.dist.isClient()) {
				File dir = this.manager.getSidedDirectory();
				if (dir != null && dir.exists()) {
					try {
						FileUtils.deleteDirectory(dir);
					} catch (IOException e) {
						//TODO: Log
						e.printStackTrace();
					}
				}
			}
		}
		
		for(Tuple<ResourceLocation, byte[]> tuple : this) {
			final ResourceLocation id = tuple.getA();
			final byte[] data = tuple.getB();
			
			if (!this.manager.writeFile(id, data)) {
				//TODO: Log
			}
		}
		
		Minecraft.getInstance().reloadResourcePacks();
	}

}
