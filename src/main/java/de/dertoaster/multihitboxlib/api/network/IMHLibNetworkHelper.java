package de.dertoaster.multihitboxlib.api.network;

import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.lang.reflect.InvocationTargetException;

public interface IMHLibNetworkHelper {

    // Reimplementation of the questionable packet system :>
    public static <P extends IMHLibCustomPacketPayload, HC extends IMHLibCustomPacketHandler<P>, HS extends IMHLibCustomPacketHandler<P>> void register(final PayloadRegistrar register, Class<P> packetClass, Class<HS> serverHandlerClass, Class<HC> clientHandlerClass) {
        register(register, packetClass, serverHandlerClass, clientHandlerClass, true);
    }

    public static <P extends IMHLibCustomPacketPayload, HC extends IMHLibCustomPacketHandler<P>, HS extends IMHLibCustomPacketHandler<P>> void register(final PayloadRegistrar register, Class<P> packetClass, Class<HS> serverHandlerClass, Class<HC> clientHandlerClass, boolean playOrCommon) {
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
                    register.playBidirectional(packet.getType(), packet.getStreamCodec(), new DirectionalPayloadHandler<P>(clientHandler::handle, serverHandler::handle));
                } else {
                    register.commonBidirectional(packet.getType(), packet.getStreamCodec(), new DirectionalPayloadHandler<P>(clientHandler::handle, serverHandler::handle));
                }
            }
            // S2C
            else if (clientHandler != null) {
                if (playOrCommon) {
                    register.playToClient(packet.getType(), packet.getStreamCodec(), clientHandler::handle);
                } else {
                    register.commonToClient(packet.getType(), packet.getStreamCodec(), clientHandler::handle);
                }
            }
            // C2S
            else {
                if (playOrCommon) {
                    register.playToServer(packet.getType(), packet.getStreamCodec(), serverHandler::handle);
                } else {
                    register.commonToServer(packet.getType(), packet.getStreamCodec(), serverHandler::handle);
                }
            }
        }
    }

    public static <P extends IMHLibCustomPacketPayload, H extends IMHLibCustomPacketHandler<P>>void registerC2S(final PayloadRegistrar register, Class<P> packetClass, Class<H> handlerClass) {
        register(register, packetClass, handlerClass, null);
    }

    public static <P extends IMHLibCustomPacketPayload, H extends IMHLibCustomPacketHandler<P>>void registerS2C(final PayloadRegistrar register, Class<P> packetClass, Class<H> handlerClass) {
        register(register, packetClass, null, handlerClass);
    }

}
