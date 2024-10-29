package de.dertoaster.multihitboxlib.init;

import de.dertoaster.multihitboxlib.api.network.PacketC2S;
import de.dertoaster.multihitboxlib.api.network.PacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.ArrayList;
import java.util.List;

/**
 * A new networking system!
 * <br><br>
 *
 * You will need to use two different classes depending on what you want to do: {@link PacketC2S} and {@link PacketS2C}.
 * <br><br>
 *
 * {@link PacketC2S} is used for packets sent from the client to the server <br>
 * {@link PacketS2C} is used for packets sent from the server to the client <br>
 *
 * <br><br>
 *
 * To use this system, you will need to create a new class that extends {@link PacketC2S} or {@link PacketS2C}
 * and implement the required methods. If you want to know more about this system please the docs for these classes.
 */
public final class MHLibPackets {
    private static final List<PacketC2S> C2S_PACKETS = new ArrayList<>();
    private static final List<PacketS2C> S2C_PACKETS = new ArrayList<>();

    /**
     * Internal use only. Do not use this method.
     * <br><br>
     *
     * Register a packet to be sent from the client to the server
     * @param packet The packet to be sent
     */
    public static void registerClientToServerPacket(PacketC2S packet) {
        C2S_PACKETS.add(packet);
    }

    /**
     * Internal use only. Do not use this method.
     * <br><br>
     *
     * Register a packet to be sent from the server to the client
     * @param packet The packet to be sent
     */
    public static void registerServerToClientPacket(PacketS2C packet) {
        S2C_PACKETS.add(packet);
    }

    /**
     * Internal use only. Do not use this method.
     * <br><br>
     *
     * Register all packets to be received from the client to the server
     */
    public static void registerReceiveClientToServer(){
        for(PacketC2S packet : C2S_PACKETS){
            ServerPlayNetworking.registerGlobalReceiver(packet.getChannelName(), (server, player, handler, buf, responseSender) ->
                    server.execute(() -> packet.receive(server, player, handler, buf, responseSender)));
        }
    }

    /**
     * Internal use only. Do not use this method.
     * <br><br>
     *
     * Register a packet to be sent from the server to the client
     */
    public static void registerReceiveServerToClient(){
        for(PacketS2C packet : S2C_PACKETS){
            ClientPlayNetworking.registerGlobalReceiver(packet.getChannelName(), (client, handler, buf, responseSender) ->
                    client.execute(() -> packet.receive(client, handler, buf, responseSender)));
        }
    }
}
