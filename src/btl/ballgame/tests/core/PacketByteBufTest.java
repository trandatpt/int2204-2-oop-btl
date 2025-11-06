package btl.ballgame.tests.core;

import static org.junit.jupiter.api.Assertions.*;
import java.nio.BufferOverflowException;
import org.junit.jupiter.api.Test;

import btl.ballgame.protocol.PacketByteBuf;

public class PacketByteBufTest {
	@Test
	public void testMallocDynamicAndFixed() {
		// dynamic malloc: should grow indefinitely
		PacketByteBuf dynamic = PacketByteBuf.malloc(4);
		assertDoesNotThrow(() -> {
			for (int i = 0; i < 1000; i++) {
				dynamic.writeInt32(i);
			}
		});

		// fixed malloc, should error out on writing 4 to 2 bytes
		PacketByteBuf fixed = PacketByteBuf.mallocFixed(2);
		assertThrows(BufferOverflowException.class, () -> fixed.writeInt32(36));
	}

	@Test
	public void testVarUIntEncoding() {
		int[] testValues = { 0, 1, 127, 128, 255, 300, 16384, 2097151, 268435455 };
		PacketByteBuf buf = PacketByteBuf.malloc(4); // dynamic buffer
		for (int val : testValues) {
			buf.writeVarUInt(val);
		}
		
		// check integrity
		buf = PacketByteBuf.consume(buf.dump());
		for (int val : testValues) {
			assertEquals(val, buf.readVarUInt(), "VarUInt mismatch for value: " + val);
		}
	}

	@Test
	public void testUTF8StringEncoding() {
		PacketByteBuf buf = PacketByteBuf.malloc(128);
		String[] testStrings = { null, "", "Hello", "фрукты", "банка" };
		for (String s : testStrings) {
			buf.writeU8String(s);
		}
		
		// check integrity
		buf = PacketByteBuf.consume(buf.dump());
		for (String s : testStrings) {
			assertEquals(s, buf.readU8String());
		}
	}

	@Test
	public void testUTF16StringEncoding() {
		PacketByteBuf buf = PacketByteBuf.malloc(100);
		String[] testStrings = { null, 
			"練習超自然現象", 
			"Tôi bị ngu", 
			"Ôm phản lao ra biển", 
			"Có làm thì mới có ăn",
			"Владимир Путин"
		};
		for (String s : testStrings) {
			buf.writeU16String(s);
		}
		
		// yes
		buf = PacketByteBuf.consume(buf.dump());
		for (String s : testStrings) {
			assertEquals(s, buf.readU16String());
		}
	}

	@Test
	public void testBooleanAndInt8() {
		PacketByteBuf buf = PacketByteBuf.malloc(10);
		buf.writeBool(true);
		buf.writeBool(false);
		buf.writeInt8((byte) 42);
		buf.writeInt8((byte) -128);

		buf = PacketByteBuf.consume(buf.dump());
		assertTrue(buf.readBool());
		assertFalse(buf.readBool());
		assertEquals(42, buf.readInt8());
		assertEquals(-128, buf.readInt8());
	}

}
