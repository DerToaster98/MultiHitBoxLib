package de.dertoaster.multihitboxlib;

import de.dertoaster.multihitboxlib.partentityimp.IEntityInterface;
import de.dertoaster.multihitboxlib.partentityimp.PartEntity;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class PartEntityManager {
    public static boolean isMultipartEntity(Entity entity){
        return ((IEntityInterface)entity).multipart$isMultipartEntity();
    }

    public static @Nullable PartEntity<?>[] getParts(Entity entity){
        return ((IEntityInterface)entity).multipart$getParts();
    }
}
