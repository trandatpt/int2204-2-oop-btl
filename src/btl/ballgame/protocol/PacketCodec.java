package btl.ballgame.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.UnknownPacketException;

/**
 * {@code PacketCodec} is the core encoder/decoder for the
 * <b>PennyWort Protocol (PWP)</b>, the network protocol used by
 * the Arkanoid game server and client to exchange packets.
 *
 * <h3>Packet Format (PWP v0.36)</h3>
 * Each packet is serialized as follows:
 * <pre>
 * [int32 length]        - Total size of the following buffer (bytes)
 * [int32 packetId]      - 32-bit ID identifying the packet type
 * [payload bytes...]    - Packet payload
 * </pre>
 *
 * <p>On the sending side, the codec writes the packet ID and payload
 * into a {@link PacketByteBuf}, then sends it with a length prefix.
 * On the receiving side, it reads the buffer, extracts the ID,
 * and uses the {@link PacketRegistry} to reconstruct the proper
 * {@link NetworkPacket} subclass.
 */
public class PacketCodec {
	/** registry maps: packet IDs -> classes and constructors. */
	private PacketRegistry registry;
	
	/**
	 * Constructs a codec bound to a specific {@link PacketRegistry}.
	 *
	 * @param registry The registry containing all PWP packet definitions.
	 */
	public PacketCodec(PacketRegistry registry) {
		this.registry = registry;
	}
	
	/**
	 * Encodes and writes a packet to the output stream following the PWP format.
	 *
	 * @param out    The output stream to write to (usually a socket stream).
	 * @param packet The packet to encode and send.
	 * @throws IOException if an I/O error occurs while writing.
	 */
	public void writePacket(DataOutputStream out, NetworkPacket packet) throws IOException {
		PacketByteBuf buf = PacketByteBuf.malloc(36); // initial, dynamic buffer of 32 + 4 bytes
		
		buf.writeInt32(registry.packetToId(packet.getClass())); // the packet ID
		packet.write(buf); // the packet payload
		
		byte buffer[] = buf.dump(); 
		out.writeInt(buffer.length); // the packet size
		out.write(buffer); // the buffer
	}
	
	/**
	 * Reads and decodes a packet from the input stream following the PWP format.
	 * <p>
	 * This method blocks until a full packet is received.
	 *
	 * @param in  The input stream to read from.
	 * @param <T> Type of the expected packet.
	 * @return A fully constructed {@link NetworkPacket} instance.
	 * @throws IOException            if an I/O error occurs while reading.
	 * @throws UnknownPacketException if the packet ID is not registered.
	 */
	@SuppressWarnings("unchecked")
	public <T extends NetworkPacket> T readPacket(DataInputStream in) throws IOException, UnknownPacketException {
		PacketByteBuf buf = PacketByteBuf.consume(in);
		int packetId = buf.readInt32();
		
		NetworkPacket networkPacket = registry.create(packetId);
		networkPacket.read(buf);
		
		return (T) networkPacket;
	}
}
