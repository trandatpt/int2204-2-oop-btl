package btl.ballgame.protocol.packets.out;

import java.util.UUID;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;

public class PacketPlayOutLoginAck extends NetworkPacket implements IPacketPlayOut {
	private boolean success;
	private UUID userUuid;
	private String errorMessage;
	
	public PacketPlayOutLoginAck() {};
	
	// when the server ACCEPTS the client, return the UUID
	public PacketPlayOutLoginAck(UUID userUUID) {
		this.success = true;
		this.userUuid = userUUID;
		this.errorMessage = null;
	}
	
	// the server DENIES the client, for many reasons
	public PacketPlayOutLoginAck(String errorMessage) {
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
		buffer.writeBool(this.success);
		if (!this.success) {
			buffer.writeU16String(errorMessage);
		} else {
			buffer.writeU8String(userUuid.toString());
		}
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.success = buffer.readBool();
		if (!this.success) {
			this.errorMessage = buffer.readU16String();
		} else {
			this.userUuid = UUID.fromString(buffer.readU8String());
		}
	}
 }
