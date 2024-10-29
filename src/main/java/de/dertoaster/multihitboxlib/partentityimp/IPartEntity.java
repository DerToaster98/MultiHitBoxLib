package de.dertoaster.multihitboxlib.partentityimp;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public interface IPartEntity<T extends Entity> {
    T getParent();
    Packet<ClientGamePacketListener> getAddEntityPacket();
}
