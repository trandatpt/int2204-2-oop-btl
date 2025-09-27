package btl.ballgame.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketByteBuf {
	public static PacketByteBuf malloc(int size) {
		return new PacketByteBuf(size, true);
	}
	
	public static PacketByteBuf mallocFixed(int size) {
		return new PacketByteBuf(size, false);
	}
	
	public static PacketByteBuf consume(DataInputStream in) throws IOException {
		int length = in.readInt();
		byte[] data = new byte[length];
		in.readFully(data);
		return new PacketByteBuf(ByteBuffer.wrap(data), true);
	}
	
	private boolean dynamicLength;
	private ByteBuffer backend;
	
	private PacketByteBuf(int size, boolean dynaLen) {
		this.backend = ByteBuffer.allocateDirect(size);
		this.dynamicLength = dynaLen;
	}
	
	private PacketByteBuf(ByteBuffer bytes, boolean fixed) {
		this.dynamicLength = fixed;
		this.backend = bytes;
	}
	
	// WRITE
	public void writeInt8(byte b) {
		ensureCapacity(Byte.BYTES);
		backend.put(b);
	}
	
	public void writeInt16(short s) {
		ensureCapacity(Short.BYTES);
		backend.putShort(s);
	}
	
	public void writeUint16(char c) {
		ensureCapacity(Character.BYTES);
		backend.putChar(c);
	}
	
	public void writeInt32(int i) {
		ensureCapacity(Integer.BYTES);
		backend.putInt(i);
	}
	
	public void writeInt64(long l) {
		ensureCapacity(Long.BYTES);
		backend.putLong(l);
	}

	public void writeU16String(String str) {
		ensureCapacity(Integer.BYTES + str.length() * Character.BYTES);
		writeInt32(str.length());
		for (int i = 0; i < str.length(); i++) {
			writeUint16(str.charAt(i));
		}
	}
	
	public void writeU8String(String str) {
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		ensureCapacity(Integer.BYTES + bytes.length);
		writeInt32(bytes.length);
		backend.put(bytes);
	}
	
	// READ OPS
	public byte readInt8() {
		return backend.get();
	}
	
	public short readInt16() {
		return backend.getShort();
	}
	
	public char readUint16() {
		return backend.getChar();
	}
	
	public int readInt32() {
		return backend.getInt();
	}
	
	public long readInt64() {
		return backend.getLong();
	}
	
	public String readU16String() {
		char buf[] = new char[readInt32()];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = readUint16();
		}
		return new String(buf);
	}
	
	public String readU8String() {
		int len = readInt32();
		byte[] bytes = new byte[len];
		backend.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	// BYTE SHENANIGANS
	public byte[] dump() {
		byte[] data = new byte[backend.position()];
		backend.flip();
		backend.get(data);
		return data;
	}
	
	public void reserve(int newCapacity) {
		if (newCapacity < this.backend.capacity()) {
			throw new IllegalArgumentException("The new buffer size is smaller than the old one!");
		}
		
		if (newCapacity == this.backend.capacity()) {
			return; // doesnt do anything
		}
		
		ByteBuffer newBuf = ByteBuffer.allocateDirect(newCapacity).order(backend.order());
		
		backend.flip(); // reset the pointer to 0 to begin copying
		newBuf.put(this.backend); // copy old -> new
		
		this.backend = newBuf; // replace the old buffer
	}
	
	private void ensureCapacity(int needed) {
		if (this.backend.remaining() >= needed) return;
		if (!this.dynamicLength) {
			throw new BufferOverflowException();
		}
		this.reserve(Math.max(backend.capacity() * 2, backend.position() + needed));
	}
}
