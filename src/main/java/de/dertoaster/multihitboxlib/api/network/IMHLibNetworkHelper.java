package de.dertoaster.multihitboxlib.api.network;

import java.lang.reflect.InvocationTargetException;

import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public interface IMHLibNetworkHelper {

    // Reimplementation of the questionable packet system :>
    public static <P extends IMHLibCustomPacketPayload<P>, HC extends IMHLibCustomPacketHandler<P>, HS extends IMHLibCustomPacketHandler<P>> void register(final PayloadRegistrar register, Class<P> packetClass, Class<HS> serverHandlerClass, Class<HC> clientHandlerClass) {
        register(register, packetClass, serverHandlerClass, clientHandlerClass, true);
    }

    public static <P extends IMHLibCustomPacketPayload<P>, HC extends IMHLibCustomPacketHandler<P>, HS extends IMHLibCustomPacketHandler<P>> void register(final PayloadRegistrar register, Class<P> packetClass, Class<HS> serverHandlerClass, Class<HC> clientHandlerClass, boolean playOrCommon) {
        P packet = null;
        HC clientHandler = null;
        HS serverHandler = null;
        try {
            packet = packetClass.getConstructor(new Class[] {}).newInstance();
            if (clientHandlerClass != null) {
                clientHandler = clientHandlerClass.getConstructor(new Class[] {}).newInstance();
            }
            if (serverHandlerClass != null) {
                serverHandler = serverHandlerClass.getConstructor(new Class[] {}).newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (packet != null && !(clientHandler == null && serverHandler == null)) {
            // Bi
            if (clientHandler != null && serverHandler != null) {
                if (playOrCommon) {
                    register.playBidirectional(packet._castType(), packet.getStreamCodec(), new DirectionalPayloadHandler<P>(clientHandler::handle, serverHandler::handle));
                } else {
                    register.commonBidirectional(packet._castType(), packet.getStreamCodec(), new DirectionalPayloadHandler<P>(clientHandler::handle, serverHandler::handle));
                }
            }
            // S2C
            else if (clientHandler != null) {
                if (playOrCommon) {
                    register.playToClient(packet._castType(), packet.getStreamCodec(), clientHandler);
                } else {
                    register.commonToClient(packet._castType(), packet.getStreamCodec(), clientHandler);
                }
            }
            // C2S
            else {
                if (playOrCommon) {
                    register.playToServer(packet._castType(), packet.getStreamCodec(), serverHandler);
                } else {
                    register.commonToServer(packet._castType(), packet.getStreamCodec(), serverHandler);
                }
            }
        }
    }

    public static <P extends IMHLibCustomPacketPayload<P>, H extends IMHLibCustomPacketHandler<P>>void registerC2S(final PayloadRegistrar register, Class<P> packetClass, Class<H> handlerClass) {
        register(register, packetClass, handlerClass, null);
    }

    public static <P extends IMHLibCustomPacketPayload<P>, H extends IMHLibCustomPacketHandler<P>>void registerS2C(final PayloadRegistrar register, Class<P> packetClass, Class<H> handlerClass) {
        register(register, packetClass, null, handlerClass);
    }

}
