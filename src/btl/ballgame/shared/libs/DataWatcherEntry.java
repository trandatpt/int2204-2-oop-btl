package btl.ballgame.shared.libs;

import btl.ballgame.protocol.PacketByteBuf;

public class DataWatcherEntry {
	public final int keyId;
	public final byte typeId;
	public final Object value;

	public DataWatcherEntry(int keyId, byte typeId, Object value) {
		this.keyId = keyId;
		this.typeId = typeId;
		this.value = value;
	}

	public static DataWatcherEntry read(PacketByteBuf buffer) {
		int keyId = buffer.readInt32();
		byte typeId = buffer.readInt8();
		Object value;
		
		value = switch (typeId) {
			case 0 -> buffer.readInt8();
			case 1 -> buffer.readInt16();
			case 2 -> buffer.readInt32();
			case 3 -> buffer.readFloat32();
			case 4 -> buffer.readU8String();
			default -> throw new IllegalArgumentException("Unknown type id: " + typeId);
		};
		
		return new DataWatcherEntry(keyId, typeId, value);
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeInt32(keyId);
		buffer.writeInt8(typeId);
		switch (typeId) {
			case 0 -> buffer.writeInt8((byte) value);
			case 1 -> buffer.writeInt16((short) value);
			case 2 -> buffer.writeInt32((int) value);
			case 3 -> buffer.writeFloat32((float) value);
			case 4 -> buffer.writeU8String((String) value);
			default -> throw new IllegalArgumentException("Unknown type id: " + typeId);
		}
	}
}
