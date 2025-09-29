package btl.ballgame.protocol.packets;

import btl.ballgame.protocol.PacketByteBuf;

/**
 * <h1>NetworkPacket — Base Class for PennyWort Protocol Packets</h1>
 *
 * <p>{@code NetworkPacket} is the fundamental abstraction for every data packet
 * transmitted through the <b>PennyWort Protocol</b>. Each inbound or outbound
 * packet exchanged between the client and server must extend this class and
 * implement its serialization and deserialization logic.</p>
 */
public abstract class NetworkPacket {
	/**
	 * Serializes this packet into a {@link PacketByteBuf} for network transmission.
	 * The order and format of fields written here <b>must</b> match what
	 * {@link #read(PacketByteBuf)} expects.
	 *
	 * @param buffer target buffer to write into
	 */
	public abstract void write(PacketByteBuf buffer);
	

	/**
	 * Deserializes this packet from a {@link PacketByteBuf} received from the
	 * network. The read order <b>must</b> match the order in
	 * {@link #write(PacketByteBuf)}.
	 *
	 * @param buffer source buffer containing the packet data
	 */
	public abstract void read(PacketByteBuf buffer);
}
