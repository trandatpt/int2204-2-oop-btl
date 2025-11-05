package btl.ballgame.protocol.packets.out;

import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.shared.libs.Constants.EnumTitle;

public class PacketPlayOutTitle extends NetworkPacket implements IPacketPlayOut {
	private static final int BOLD_MASK = 0b001;
	private static final int ITALIC_MASK = 0b010;
	private static final int UNDERLINE_MASK = 0b100;

	private String payload;
	private int color;
	private int size;
	private int yOffset;
	private int fadeInTicks;
	private int persistentTicks;
	private int fadeOutTicks;
	private byte textPreferences;

	public PacketPlayOutTitle() {}
	
	// PRESET
	public PacketPlayOutTitle(
		EnumTitle type, 
		String message, 
		int color, 
		int size,
		boolean bold, 
		boolean italic, 
		boolean underline,
		int fadeInTicks, 
		int persistentTicks,
		int fadeOutTicks
	) {
		this(switch (type) {
				case PRETITLE -> 30;
				case TITLE -> 55;
				case SUBTITLE -> 85;
			}, message, 
			color, size, 
			bold, italic, underline, 
			fadeInTicks, persistentTicks, fadeOutTicks
		);
	}

	public PacketPlayOutTitle(
		int yOffset, 
		String message, 
		int color, 
		int size,
		boolean bold, 
		boolean italic, 
		boolean underline,
		int fadeInTicks, 
		int persistentTicks,
		int fadeOutTicks
	) {
		this.payload = message;
		this.color = color;
		this.size = size;
		this.yOffset = yOffset;
		this.fadeInTicks = fadeInTicks;
		this.persistentTicks = persistentTicks;
		this.fadeOutTicks = fadeOutTicks;
		if (bold) textPreferences |= BOLD_MASK;
		if (italic) textPreferences |= ITALIC_MASK;
		if (underline) textPreferences |= UNDERLINE_MASK;
	}
	
	public int getYOffset() {
		return this.yOffset;
	}

	public String getMessage() {
		return payload;
	}

	public int getColor() {
		return color;
	}

	public int getSize() {
		return size;
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

	public boolean isBold() {
		return (textPreferences & BOLD_MASK) != 0;
	}

	public boolean isItalic() {
		return (textPreferences & ITALIC_MASK) != 0;
	}

	public boolean isUnderline() {
		return (textPreferences & UNDERLINE_MASK) != 0;
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeVarUInt(this.yOffset);
		buffer.writeVarUInt(this.fadeInTicks);
		buffer.writeVarUInt(this.fadeOutTicks);
		buffer.writeVarUInt(this.persistentTicks);
		buffer.writeInt32(this.color);
		buffer.writeVarUInt(this.size);
		buffer.writeInt8(this.textPreferences);
		buffer.writeU8String(payload);
	}

	@Override
	public void read(PacketByteBuf buffer) {
		this.yOffset = buffer.readVarUInt();
		this.fadeInTicks = buffer.readVarUInt();
		this.fadeOutTicks = buffer.readVarUInt();
		this.persistentTicks = buffer.readVarUInt();
		this.color = buffer.readInt32();
		this.size = buffer.readVarUInt();
		this.textPreferences = buffer.readInt8();
		this.payload = buffer.readU8String();
	}
}
