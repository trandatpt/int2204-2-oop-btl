package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutSocketClose extends NetworkPacket implements IPacketPlayOut {
	private static final long serialVersionUID = 1L;
	
	private String reasonString;
	
	public PacketPlayOutSocketClose(String reason) {
		this.reasonString = reason;
	}
	
	public String getReason() {
		return this.reasonString;
	}
 }
