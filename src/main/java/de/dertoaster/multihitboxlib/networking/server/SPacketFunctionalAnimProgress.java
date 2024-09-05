package de.dertoaster.multihitboxlib.networking.server;

import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.alibplus.IExtendedGeoAnimatableEntity;
import de.dertoaster.multihitboxlib.api.alibplus.WrappedAnimationController;
import de.dertoaster.multihitboxlib.api.network.PacketS2C;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class SPacketFunctionalAnimProgress extends PacketS2C {
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MHLibMod.MODID, "functional_anim_progress");
    private final String wrappedControllerName;
    private final int animatableOwnerId;
    private final double clientUpdateTickDelta;

    public SPacketFunctionalAnimProgress(ServerPlayer player, String wrappedControllerName, int animatableOwnerId, double clientUpdateTickDelta) {
        super(player, PacketByteBufs.empty());
        this.wrappedControllerName = wrappedControllerName;
        this.animatableOwnerId = animatableOwnerId;
        this.clientUpdateTickDelta = clientUpdateTickDelta;
    }

    @Override
    public void send() {
        getBuf().writeUtf(wrappedControllerName);
        getBuf().writeInt(animatableOwnerId);
        getBuf().writeDouble(clientUpdateTickDelta);

        ServerPlayNetworking.send(getPlayer(), getChannelName(), getBuf());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        String wrappedControllerName = buf.readUtf();
        int animatableOwnerId = buf.readInt();
        double clientUpdateTickDelta = buf.readDouble();
        Entity targetPotentialAnimatable = client.level.getEntity(animatableOwnerId);

        if (!(targetPotentialAnimatable instanceof IExtendedGeoAnimatableEntity) || targetPotentialAnimatable == null) return;

        IExtendedGeoAnimatableEntity targetAnimatable = (IExtendedGeoAnimatableEntity) targetPotentialAnimatable;
        WrappedAnimationController<IExtendedGeoAnimatableEntity> targetWrappedController = targetAnimatable.getWrappedControllerByName(wrappedControllerName);
    }

    @Override
    public ResourceLocation getChannelName() {
        return CHANNEL_NAME;
    }
}
