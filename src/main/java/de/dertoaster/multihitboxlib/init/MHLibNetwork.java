package de.dertoaster.multihitboxlib.init;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.network.IMHLibNetworkHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MODID)
public class MHLibNetwork implements IMHLibNetworkHelper {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar NETWORK = event.registrar(Constants.NETWORK_VERSION);

    }
}
