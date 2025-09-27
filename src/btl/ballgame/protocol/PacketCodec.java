package btl.ballgame.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.UnknownPacketException;

public class PacketCodec {
	private PacketRegistry registry;
	
	public PacketCodec(PacketRegistry registry) {
		this.registry = registry;
	}
	
	public void writePacket(DataOutputStream out, NetworkPacket packet) throws IOException {
		PacketByteBuf buf = PacketByteBuf.malloc(64);
		buf.writeInt32(registry.packetToId(packet.getClass()));
		packet.write(buf);
		
		byte buffer[] = buf.dump();
		out.writeInt(buffer.length);
		out.write(buffer);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends NetworkPacket> T readPacket(DataInputStream in) throws IOException, UnknownPacketException {
		PacketByteBuf buf = PacketByteBuf.consume(in);
		int packetId = buf.readInt32();
		
		NetworkPacket networkPacket = registry.create(packetId);
		networkPacket.read(buf);
		
		return (T) networkPacket;
	}
}
