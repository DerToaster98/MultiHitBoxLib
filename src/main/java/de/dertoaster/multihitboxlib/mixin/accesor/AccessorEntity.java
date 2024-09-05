package de.dertoaster.multihitboxlib.mixin.accesor;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Accessor("ENTITY_COUNTER")
    public AtomicInteger getEntityCounter();
}
