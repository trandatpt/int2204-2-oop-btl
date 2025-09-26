package btl.ballgame.protocol.packets.in;

import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayInClientHello extends NetworkPacket implements IPacketPlayIn {
	private static final long serialVersionUID = 1L;
	
	private final String userName;
	private final int protocolVersion;
	
	public PacketPlayInClientHello(String username, int protocolVersion) {
		this.userName = username;
		this.protocolVersion = protocolVersion;
	}
	
	public String who() {
		return this.userName;
	}
	
	public int whatVersion() {
		return this.protocolVersion;
	}
}
