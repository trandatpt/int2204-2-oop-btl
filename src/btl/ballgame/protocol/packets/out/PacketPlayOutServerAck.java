package btl.ballgame.protocol.packets.out;

import java.util.UUID;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutServerAck extends NetworkPacket implements IPacketPlayOut {
	private boolean success;
	private UUID userUuid;
	private String errorMessage;
	
	public PacketPlayOutServerAck() {};
	
	// when the server ACCEPTS the client, return the UUID
	public PacketPlayOutServerAck(UUID userUUID) {
		this.success = true;
		this.userUuid = userUUID;
		this.errorMessage = null;
	}
	
	// the server DENIES the client, for many reasons
	public PacketPlayOutServerAck(String errorMessage) {
		this.errorMessage = errorMessage;
		this.success = false;
		this.userUuid = null;
	}
	
	public boolean isSuccessful() {
		return this.success;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public UUID getServerSideUUID() {
		return this.userUuid;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		
	}

	@Override
	public void read(PacketByteBuf buffer) {
		
	}
 }
