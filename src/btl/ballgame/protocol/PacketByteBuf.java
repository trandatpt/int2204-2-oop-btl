package btl.ballgame.protocol;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * <h1>PacketByteBuf — Core ByteBuffer for PennyWort Protocol (PWP)</h1>
 *
 * <p>
 * {@code PacketByteBuf} is the fundamental data serialization and
 * deserialization utility used by the <b>PennyWort Protocol (PWP)</b> to encode
 * and decode network packets. It is a thin wrapper around a {@link ByteBuffer}
 * with a binary format optimized for deterministic, cross-platform
 * communication between server and client (Note: PacketByteBuf is big-endian).
 * </p>
 */
public class PacketByteBuf {
	/**
	 * Allocates a new dynamic-length buffer. Capacity will grow if needed.
	 *
	 * @param size initial capacity
	 * @return new {@code PacketByteBuf}
	 */
	public static PacketByteBuf malloc(int size) {
		return new PacketByteBuf(size, true);
	}
	
	/**
	 * Allocates a new fixed-length buffer. Writes beyond capacity will throw an
	 * exception.
	 *
	 * @param size buffer capacity
	 * @return new {@code PacketByteBuf}
	 */
	public static PacketByteBuf mallocFixed(int size) {
		return new PacketByteBuf(size, false);
	}
	
	/**
	 * Reads a packet buffer from a stream. This expects the first 4 bytes to be the
	 * length prefix (written by {@link PacketCodec#writePacket}).
	 *
	 * @param in input stream (socket)
	 * @return a {@code PacketByteBuf} positioned at the start of the payload
	 * @throws IOException if I/O fails
	 */
	public static PacketByteBuf consume(DataInputStream in) throws IOException {
		int length = in.readInt();
		byte[] data = new byte[length];
		in.readFully(data);
		return new PacketByteBuf(ByteBuffer.wrap(data), false);
	}
	
	private boolean dynamicLength;
	private ByteBuffer backend;
	
	private PacketByteBuf(int size, boolean dynaLen) {
		this.backend = ByteBuffer.allocateDirect(size).order(ByteOrder.BIG_ENDIAN);
		this.dynamicLength = dynaLen;
	}
	
	private PacketByteBuf(ByteBuffer bytes, boolean dynaLen) {
		this.dynamicLength = dynaLen;
		this.backend = bytes;
	}
	
	/** Writes a single boolean value (1 byte). */
	public void writeBool(boolean bool) {
		ensureCapacity(Byte.BYTES);
		backend.put((byte) (bool ? 0x1 : 0x0));
	}
	
	/** Writes an 8-bit signed integer. */
	public void writeInt8(byte b) {
		ensureCapacity(Byte.BYTES);
		backend.put(b);
	}
	
	/** Writes a 16-bit signed integer. */
	public void writeInt16(short s) {
		ensureCapacity(Short.BYTES);
		backend.putShort(s);
	}
	
	/** Writes a 16-bit unsigned integer (stored as a char). */
	public void writeUint16(char c) {
		ensureCapacity(Character.BYTES);
		backend.putChar(c);
	}
	
	/** Writes a 32-bit signed integer. */
	public void writeInt32(int i) {
		ensureCapacity(Integer.BYTES);
		backend.putInt(i);
	}
	
	/** Writes a 64-bit signed integer. */
	public void writeInt64(long l) {
		ensureCapacity(Long.BYTES);
		backend.putLong(l);
	}
	
	/** Writes a 32-bit floating point number. */
	public void writeFloat32(float f) {
		ensureCapacity(Float.BYTES);
		backend.putFloat(f);
	}
	
	/**
	 * Writes a UTF-16 string with a null flag and length prefix. Format:
	 * 
	 * <pre>
	 * [bool isNotNull][int length][char... data]
	 * </pre>
	 * 
	 * @apiNote this method is slower than {@link writeU8String}
	 */
	public void writeU16String(String str) {
		if (str == null) {
			writeBool(false); // string is null
			return;
		}
		
		ensureCapacity(Byte.BYTES + Integer.BYTES + str.length() * Character.BYTES);
		writeBool(true); // string is not null
		writeInt32(str.length());
		for (int i = 0; i < str.length(); i++) {
			writeUint16(str.charAt(i));
		}
	}
	
	/**
	 * Writes a UTF-8 string with a null flag and length prefix. Format:
	 * 
	 * <pre>
	 * [bool isNotNull][int length][uint8... utf8]
	 * </pre>
	 */
	public void writeU8String(String str) {
		if (str == null) {
			writeBool(false); // string is null
			return;
		}
		
		byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
		ensureCapacity(Byte.BYTES + Integer.BYTES + bytes.length);
		writeBool(true); // string is not null
		writeInt32(bytes.length);
		backend.put(bytes);
	}
	
	/** Reads a boolean (1 byte). */
	public boolean readBool() {
		return backend.get() != 0x0;
	}
	
	/** Reads an 8-bit signed integer. */
	public byte readInt8() {
		return backend.get();
	}
	
	/** Reads a 16-bit signed integer. */
	public short readInt16() {
		return backend.getShort();
	}
	
	/** Reads a 16-bit unsigned integer (stored as char). */
	public char readUint16() {
		return backend.getChar();
	}
	
	/** Reads a 32-bit signed integer. */
	public int readInt32() {
		return backend.getInt();
	}
	
	/** Reads a 64-bit signed integer. */
	public long readInt64() {
		return backend.getLong();
	}
	
	/** Reads a 32-bit floating point number. */
	public float readFloat32() {
		return backend.getFloat();
	}
	
	/** Reads a UTF-16 string (null-safe). */
	public String readU16String() {
		if (!readBool()) return null; // null string
		char buf[] = new char[readInt32()];
		for (int i = 0; i < buf.length; i++) {
			buf[i] = readUint16();
		}
		return new String(buf);
	}
	
	/** Reads a UTF-8 string (null-safe). */
	public String readU8String() {
		if (!readBool()) return null; // null string
		int len = readInt32();
		byte[] bytes = new byte[len];
		backend.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	/**
	 * Dumps the written portion of the buffer into a byte array. This resets the
	 * position to the beginning.
	 * 
	 * @apiNote You cannot write to this instance of {@link PacketByteBuf} after
	 * this function call.
	 *
	 * @return byte array containing all written data
	 */
	public byte[] dump() {
		byte[] data = new byte[backend.position()];
		backend.flip();
		backend.get(data);
		return data;
	}
	
	/**
	 * Resizes the buffer to a new capacity. Used internally by
	 * {@link #ensureCapacity(int)} when dynamic.
	 */
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
	
	/** Ensures there is enough space to write {@code needed} bytes. */
	private void ensureCapacity(int needed) {
		if (this.backend.remaining() >= needed) return;
		if (!this.dynamicLength) {
			throw new BufferOverflowException();
		}
		this.reserve(Math.max(backend.capacity() * 2, backend.position() + needed));
	}
}
