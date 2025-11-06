package btl.ballgame.tests.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.Collection;

import btl.ballgame.shared.libs.DataWatcher;
import btl.ballgame.shared.libs.DataWatcherEntry;
import btl.ballgame.protocol.PacketByteBuf;

public class DataWatcherTest {
	@Test
	public void testWatchAndGet() {
		DataWatcher dw = new DataWatcher();
		dw.watch((short) 1, 42);
		dw.watch((short) 2, "Rau");
		dw.watch((short) 3, true);
		
		assertEquals(42, dw.get((short) 1));
		assertEquals("Rau", dw.get((short) 2));
		assertEquals(true, dw.get((short) 3));
		assertNull(dw.get((short) 4));
		assertEquals("Ma", dw.getOrDefault((short) 4, "Ma"));
	}

	@Test
	public void testUnwatchAndClear() {
		DataWatcher dw = new DataWatcher();
		dw.watch((short) 1, 42);
		dw.watch((short) 2, "Hello");

		dw.unwatch((short) 1);
		assertNull(dw.get((short) 1));
		assertEquals("Hello", dw.get((short) 2));

		dw.clear();
		assertNull(dw.get((short) 2));
	}

	@Test
	public void testDetectTypeUnsupported() {
		DataWatcher dw = new DataWatcher();
		assertThrows(IllegalArgumentException.class, () -> dw.watch((short) 1, new Object()));
	}

	@Test
	public void testEntriesCollection() {
		DataWatcher dw = new DataWatcher();
		dw.watch((short) 1, 100);
		dw.watch((short) 2, false);

		Collection<DataWatcherEntry> entries = dw.entries();
		assertEquals(2, entries.size());
		assertTrue(entries.stream().anyMatch(e -> e.keyId == 1 && e.value.equals(100)));
		assertTrue(entries.stream().anyMatch(e -> e.keyId == 2 && e.value.equals(false)));
	}

	@Test
	public void testWriteAndReadBuffer() {
		DataWatcher dw = new DataWatcher();
		dw.watch((short) 1, 42);
		dw.watch((short) 2, "Hello");
		dw.watch((short) 3, true);

		PacketByteBuf buf = PacketByteBuf.malloc(128);
		dw.write(buf);

		PacketByteBuf readBuf = PacketByteBuf.consume(buf.dump());
		DataWatcher dw2 = new DataWatcher();
		dw2.read(readBuf);

		assertEquals(42, dw2.get((short) 1));
		assertEquals("Hello", dw2.get((short) 2));
		assertEquals(true, dw2.get((short) 3));
	}
}
