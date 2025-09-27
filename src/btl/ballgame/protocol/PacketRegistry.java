package btl.ballgame.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.shared.UnhandledPacketException;
import btl.ballgame.shared.UnknownPacketException;

/**
 * Registry for PennyWort Protocol packets and their handlers.
 * <p>
 * Maintains the mappings:
 * <ul>
 *     <li>Packet ID -> Packet factory (to create instances)</li>
 *     <li>Packet class -> Handler</li>
 *     <li>Packet class -> Packet ID</li>
 * </ul>
 * <p>
 */
public class PacketRegistry {
    /** Packet ID -> Factory to create packet instances */
    private Map<Integer, Supplier<? extends NetworkPacket>> packetFactories = new HashMap<>();
    /** Packet class -> Handler for that packet type */
	private Map<Class<? extends NetworkPacket>, 
			PacketHandler<? extends NetworkPacket, ? extends ConnectionCtx>> packetHandlers = new HashMap<>();
	/** Packet class -> Packet ID */
	private Map<Class<? extends NetworkPacket>, Integer> packetClassToId = new HashMap<>();

	/**
	 * Registers a packet in the registry.
	 *
	 * @param packetId      Unique numeric ID for this packet (use hex please)
	 * @param packetClass   The class of the packet
	 * @param packetFactory Supplier that creates new instances of the packet
	 * @param <T>           Type of the packet
	 */
	public <T extends NetworkPacket> void registerPacket(
		int packetId, Class<T> packetClass, 
		Supplier<T> packetFactory
	) {
		packetFactories.put(packetId, packetFactory);
		packetClassToId.put(packetClass, packetId);
	}
	
	/**
	 * Registers a handler for a specific INCOMING (Inbound) packet type.
	 *
	 * @param packetClass The class of the packet this handler handles
	 * @param handler     The handler instance
	 * @param <T>         Packet type
	 * @param <U>         Connection context type
	 */
	public <T extends NetworkPacket, U extends ConnectionCtx>
	void registerHandler(
		Class<T> packetClass, PacketHandler<T, U> handler
	) {
		packetHandlers.put(packetClass, handler);
	}
	
	/**
	 * Returns the numeric ID of a packet class.
	 *
	 * @param packetClass The packet class
	 * @param <T>         Packet type
	 * @return The registered packet ID
	 * @throws IllegalArgumentException if the packet class is not registered
	 */
	public <T extends NetworkPacket> int packetToId(Class<T> packet) {
		if (!packetClassToId.containsKey(packet)) {
			throw new IllegalArgumentException(packet + " is not registered!");
		}
		return packetClassToId.get(packet);
	}
	
	/**
	 * Creates a new instance of a packet by its ID.
	 *
	 * @param id The numeric packet ID
	 * @return A fresh instance of the corresponding packet
	 * @throws UnknownPacketException If the ID is not registered
	 */
	public NetworkPacket create(int id) throws UnknownPacketException {
		Supplier<? extends NetworkPacket> supplier = this.packetFactories.get(id);
		if (supplier == null) {
			throw new UnknownPacketException(id);
		}
		return supplier.get();
	}
	
	/**
	 * Retrieves the registered handler for a specific INCOMING (Inbound) packet class.
	 *
	 * @param packetClass The class of the packet
	 * @param <T>         Packet type
	 * @param <U>         Connection context type
	 * @return The handler instance
	 * @throws UnhandledPacketException If no handler is registered for this packet class
	 */
	@SuppressWarnings("unchecked")
	public <T extends NetworkPacket, U extends ConnectionCtx>
	PacketHandler<T, U> getHandle(Class<T> packetClass) throws UnhandledPacketException {
		PacketHandler<T, U> handler = (PacketHandler<T, U>) this.packetHandlers.get(packetClass);
		if (handler == null) {
			throw new UnhandledPacketException(packetClass);
		}
		return handler;
	}
}
