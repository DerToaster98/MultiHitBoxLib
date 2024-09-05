package de.dertoaster.multihitboxlib.partentityimp;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public abstract class PartEntity<T extends Entity> extends Entity implements IPartEntity<T> {
    private final T parent;

    public PartEntity(T parent) {
        super(parent.getType(), parent.level());
        this.parent = parent;
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }
}