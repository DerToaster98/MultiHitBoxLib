package de.dertoaster.multihitboxlib.network.server.assetsync;

import de.dertoaster.multihitboxlib.api.network.IMHLibCustomPacketPayload;
import de.dertoaster.multihitboxlib.assetsynch.data.SynchDataManagerData;
import de.dertoaster.multihitboxlib.init.MHLibNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SPacketSyncDataContent(
    SynchDataManagerData payload
) implements IMHLibCustomPacketPayload<SPacketSyncDataContent> {

    @Override
    public StreamCodec<FriendlyByteBuf, SPacketSyncDataContent> getStreamCodec() {
        return null;
    }

    public SPacketSyncDataContent() {
        // Nothing to do here...
        this((SynchDataManagerData) null);
    }

    public static SPacketSyncDataContent read(FriendlyByteBuf buf) {
        if (!(buf instanceof RegistryFriendlyByteBuf)) {
            throw new IllegalStateException("SPacketUpdateMultiPart can ONLY be sent on the play network channel!");
        }
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;

        return new SPacketSyncDataContent(regBuf.readJsonWithCodec(SynchDataManagerData.CODEC));
    }

    public void write(FriendlyByteBuf buf) {
        if (!(buf instanceof RegistryFriendlyByteBuf)) {
            throw new IllegalStateException("SPacketUpdateMultiPart can ONLY be sent on the play network channel!");
        }
        RegistryFriendlyByteBuf regBuf = (RegistryFriendlyByteBuf) buf;
        buf.writeJsonWithCodec(SynchDataManagerData.CODEC, this.payload());
    }

    public static final StreamCodec<FriendlyByteBuf, SPacketSyncDataContent> STREAM_CODEC = CustomPacketPayload.codec(SPacketSyncDataContent::write, SPacketSyncDataContent::read);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return MHLibNetwork.S2C_SYNCH_DATA_CONTENT;
    }
}
