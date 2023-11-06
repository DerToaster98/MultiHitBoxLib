package de.dertoaster.multihitboxlib.network.server;

import de.dertoaster.multihitboxlib.api.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;

public class SPacketFunctionalAnimProgress extends AbstractPacket<SPacketFunctionalAnimProgress> {
    private final String wrappedControllerName;
    private final int animatableOwnerId;
    private final double clientUpdateTickDelta;

    public SPacketFunctionalAnimProgress(String wrappedControllerName, int animatableOwnerId, double clientUpdateTickDelta) {
        this.wrappedControllerName = wrappedControllerName;
        this.animatableOwnerId = animatableOwnerId;
        this.clientUpdateTickDelta = clientUpdateTickDelta;
    }

    @Override
    public Class<SPacketFunctionalAnimProgress> getPacketClass() {
        return SPacketFunctionalAnimProgress.class;
    }

    @Override
    public SPacketFunctionalAnimProgress fromBytes(FriendlyByteBuf buffer) {
        return new SPacketFunctionalAnimProgress(buffer.readUtf(), buffer.readInt(), buffer.readDouble());
    }

    @Override
    public void toBytes(SPacketFunctionalAnimProgress packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.wrappedControllerName);
        buffer.writeInt(packet.animatableOwnerId);
        buffer.writeDouble(packet.clientUpdateTickDelta);
    }

    public String getWrappedControllerName() {
        return wrappedControllerName;
    }

    public int getAnimatableOwnerId() {
        return animatableOwnerId;
    }

    public double getClientUpdateTickDelta() {
        return clientUpdateTickDelta;
    }
}