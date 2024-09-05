package de.dertoaster.multihitboxlib.assetsynch;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
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