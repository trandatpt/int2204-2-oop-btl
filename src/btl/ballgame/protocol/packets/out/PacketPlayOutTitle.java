package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.EnumTitle;

public class PacketPlayOutTitle extends NetworkPacket implements IPacketPlayOut {
	public PacketPlayOutTitle() {}
	
	private String payload;
	private int color;
	private EnumTitle type;
	private int fadeInTicks, persistentTicks, fadeOutTicks;
	
	public PacketPlayOutTitle(
		EnumTitle type, 
		String message, int color,
		int fadeInTicks, 
		int persistentTicks,
		int fadeOutTicks
	) {
		this.payload = message;
		this.color = color;
		this.type = type;
		this.fadeInTicks = fadeInTicks;
		this.persistentTicks = persistentTicks;
		this.fadeOutTicks = fadeOutTicks;
	}
	
	public EnumTitle getType() {
		return type;
	}
	
	public String getMessage() {
		return payload;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getFadeIn() {
		return fadeInTicks;
	}
	
	public int getFadeOut() {
		return fadeOutTicks;
	}
	
	public int getPersistent() {
		return persistentTicks;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt8((byte) type.ordinal());
		buffer.writeVarUInt(this.fadeInTicks);
		buffer.writeVarUInt(this.fadeOutTicks);
		buffer.writeVarUInt(this.persistentTicks);
		buffer.writeInt32(this.color);
		buffer.writeU8String(payload);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.type = EnumTitle.values()[buffer.readInt8()];
		this.fadeInTicks = buffer.readVarUInt();
		this.fadeOutTicks = buffer.readVarUInt();
		this.persistentTicks = buffer.readVarUInt();
		this.color = buffer.readInt32();
		this.payload = buffer.readU8String();
	}
 }
