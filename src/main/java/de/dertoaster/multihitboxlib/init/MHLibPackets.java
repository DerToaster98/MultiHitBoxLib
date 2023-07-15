package de.dertoaster.multihitboxlib.init;

import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.network.IMessage;
import de.dertoaster.multihitboxlib.api.network.IMessageHandler;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.client.CPacketHandlerSetMaster;
import de.dertoaster.multihitboxlib.network.client.CPacketHandlerUpdateMultipart;
import de.dertoaster.multihitboxlib.network.client.datapacksync.CPacketHandlerSyncHitboxProfile;
import de.dertoaster.multihitboxlib.network.server.SPacketHandlerBoneInformation;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import de.dertoaster.multihitboxlib.network.server.datapacksync.SPacketSyncHitboxProfile;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;

public class MHLibPackets {

	public static final SimpleChannel MHLIB_NETWORK = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MODID, "main"), () -> Constants.NETWORK_VERSION, Constants.NETWORK_VERSION::equals, Constants.NETWORK_VERSION::equals);

	// Start the IDs at 1 so any unregistered messages (ID 0) throw a more obvious exception when received
	private static int messageID = 0;

	public static void init() {
		registerClientToServer(CPacketBoneInformation.class, SPacketHandlerBoneInformation.class);
		
		registerServerToClient(SPacketSyncHitboxProfile.class, CPacketHandlerSyncHitboxProfile.class);
		registerServerToClient(SPacketSetMaster.class, CPacketHandlerSetMaster.class);
		registerServerToClient(SPacketUpdateMultipart.class, CPacketHandlerUpdateMultipart.class);
	}
	
	public static <T extends Object> void send(T packet, PacketTarget target) {
		MHLIB_NETWORK.send(target, packet);
	}
	
	public static <T extends Object> void sendToServer(T packet) {
		MHLIB_NETWORK.sendToServer(packet);
	}
	
	protected static <MSG> void registerClientToServer(Class<? extends IMessage<MSG>> clsMessage, Class<? extends IMessageHandler<MSG>> clsHandler) {
		register(clsMessage, clsHandler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
	}

	protected static <MSG> void registerServerToClient(Class<? extends IMessage<MSG>> clsMessage, Class<? extends IMessageHandler<MSG>> clsHandler) {
		register(clsMessage, clsHandler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}

	protected static <MSG> void register(Class<? extends IMessage<MSG>> clsMessage, Class<? extends IMessageHandler<MSG>> clsHandler) {
		register(clsMessage, clsHandler, Optional.empty());
	}

	protected static <MSG> void register(Class<? extends IMessage<MSG>> clsMessage, Class<? extends IMessageHandler<MSG>> clsHandler, final Optional<NetworkDirection> networkDirection) {
		IMessage<MSG> message = null;
		try {
			message = clsMessage.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		IMessageHandler<MSG> handler = null;
		try {
			handler = clsHandler.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		assert (handler != null && message != null);
		register(message, handler, networkDirection);
	}
	
	protected static <MSG> void register(IMessage<MSG> message, IMessageHandler<MSG> handler) {
		register(message, handler, Optional.empty());
	}

	protected static <MSG> void register(IMessage<MSG> message, IMessageHandler<MSG> handler, final Optional<NetworkDirection> networkDirection) {
		MHLIB_NETWORK.registerMessage(messageID++, message.getPacketClass(), message::toBytes, message::fromBytes, handler::handlePacket, networkDirection);
	}

}
