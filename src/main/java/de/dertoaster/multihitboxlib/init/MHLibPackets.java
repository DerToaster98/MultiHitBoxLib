package de.dertoaster.multihitboxlib.init;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import de.dertoaster.multihitboxlib.Constants;
import de.dertoaster.multihitboxlib.api.network.IMessage;
import de.dertoaster.multihitboxlib.api.network.IMessageHandler;
import de.dertoaster.multihitboxlib.network.client.CPacketBoneInformation;
import de.dertoaster.multihitboxlib.network.client.SPacketHandlerSetMaster;
import de.dertoaster.multihitboxlib.network.client.SPacketHandlerUpdateMultipart;
import de.dertoaster.multihitboxlib.network.client.assetsync.SPacketHandlerSynchAssets;
import de.dertoaster.multihitboxlib.network.client.datapacksync.SPacketHandlerSyncHitboxProfile;
import de.dertoaster.multihitboxlib.network.server.CPacketHandlerBoneInformation;
import de.dertoaster.multihitboxlib.network.server.SPacketSetMaster;
import de.dertoaster.multihitboxlib.network.server.SPacketUpdateMultipart;
import de.dertoaster.multihitboxlib.network.server.assetsync.SPacketSynchAssets;
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
		registerClientToServer(CPacketBoneInformation.class, CPacketHandlerBoneInformation.class);
		
		registerServerToClient(SPacketSyncHitboxProfile.class, SPacketHandlerSyncHitboxProfile.class);
		registerServerToClient(SPacketSetMaster.class, SPacketHandlerSetMaster.class);
		registerServerToClient(SPacketUpdateMultipart.class, SPacketHandlerUpdateMultipart.class);
		
		// Asset Synch
		registerServerToClient(SPacketSynchAssets.class, SPacketHandlerSynchAssets.class);
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
		IMessageHandler<MSG> handler = null;
		try {
			message = clsMessage.getConstructor(new Class[] {}).newInstance();
			handler = clsHandler.getConstructor(new Class[] {}).newInstance();
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
		if (handler != null && message != null)
			register(message, handler, networkDirection);
	}
	
	protected static <MSG> void register(IMessage<MSG> message, IMessageHandler<MSG> handler) {
		register(message, handler, Optional.empty());
	}

	protected static <MSG> void register(IMessage<MSG> message, IMessageHandler<MSG> handler, final Optional<NetworkDirection> networkDirection) {
		MHLIB_NETWORK.registerMessage(messageID++, message.getPacketClass(), message::toBytes, message::fromBytes, handler::handlePacket, networkDirection);
	}

}
