package de.dertoaster.multihitboxlib.init;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.MHLibMod;
import de.dertoaster.multihitboxlib.api.network.IMHLibNetworkHelper;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.client.SPacketHandlerFunctionalAnimProgress;
import de.dertoaster.multihitboxlib.network.client.SPacketHandlerSetMaster;
import de.dertoaster.multihitboxlib.network.client.SPacketHandlerUpdateMultipart;
import de.dertoaster.multihitboxlib.network.client.assetsync.CPacketRequestSynch;
import de.dertoaster.multihitboxlib.network.server.CPacketHandlerBoneInformation;
import de.dertoaster.multihitboxlib.network.server.SPacketFunctionalAnimProgress;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import de.dertoaster.multihitboxlib.network.server.assetsync.CPacketHandlerRequestSynch;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MODID)
public class MHLibNetwork implements IMHLibNetworkHelper {

    public static final CustomPacketPayload.Type<CPacketRequestSynch> C2S_REQUEST_SYNCH = new CustomPacketPayload.Type<>(MHLibMod.prefix("c2s_request_synch"));
    public static final CustomPacketPayload.Type<CPacketBoneInformation> C2S_BONE_INFORMATION = new CustomPacketPayload.Type<>(MHLibMod.prefix("c2s_bone_information"));

    public static final CustomPacketPayload.Type<SPacketSetMaster> S2C_SET_MASTER = new CustomPacketPayload.Type<>(MHLibMod.prefix("s2c_set_master"));
    public static final CustomPacketPayload.Type<SPacketUpdateMultipart> S2C_UPDATE_MULTIPART = new CustomPacketPayload.Type<>(MHLibMod.prefix("s2c_update_multipart"));
    public static final CustomPacketPayload.Type<SPacketFunctionalAnimProgress> S2C_FUNCTIONAL_ANIM_PROGRESS = new CustomPacketPayload.Type<>(MHLibMod.prefix("s2c_functional_anim_progress"));
    

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar NETWORK = event.registrar(Constants.NETWORK_VERSION);

        IMHLibNetworkHelper.registerC2S(NETWORK, CPacketRequestSynch.class, CPacketHandlerRequestSynch.class);
        IMHLibNetworkHelper.registerC2S(NETWORK, CPacketBoneInformation.class, CPacketHandlerBoneInformation.class);

        IMHLibNetworkHelper.registerS2C(NETWORK, SPacketSetMaster.class, SPacketHandlerSetMaster.class);
        IMHLibNetworkHelper.registerS2C(NETWORK, SPacketUpdateMultipart.class, SPacketHandlerUpdateMultipart.class);
        IMHLibNetworkHelper.registerS2C(NETWORK, SPacketFunctionalAnimProgress.class, SPacketHandlerFunctionalAnimProgress.class);
    }
}
