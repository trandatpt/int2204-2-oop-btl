package btl.ballgame.tests.protocol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import btl.ballgame.protocol.PacketRegistry;
import btl.ballgame.protocol.packets.NetworkPacket;
import btl.ballgame.protocol.packets.PacketHandler;
import btl.ballgame.protocol.ConnectionCtx;
import btl.ballgame.protocol.PacketByteBuf;
import btl.ballgame.shared.UnknownPacketException;
import btl.ballgame.shared.UnhandledPacketException;

public class PacketRegistryTest {
    private static class TestDummyPacket extends NetworkPacket {
        @Override
        public void write(PacketByteBuf buf) {}
        @Override
        public void read(PacketByteBuf buf) {}
    }

    private static class DummyHandler implements PacketHandler<TestDummyPacket, ConnectionCtx> {
        @Override
        public void handle(TestDummyPacket packet, ConnectionCtx ctx) {}
    }

    @Test
    public void testRegistryBasic() throws UnknownPacketException, UnhandledPacketException {
        PacketRegistry registry = new PacketRegistry();

        // register packet
        registry.registerPacket(1, TestDummyPacket.class, TestDummyPacket::new);
        assertEquals(1, registry.packetToId(TestDummyPacket.class));

        // create packet by ID
        NetworkPacket pkt = registry.create(1);
        assertTrue(pkt instanceof TestDummyPacket);

        // unknown packet ID -> ERROR
        assertThrows(UnknownPacketException.class, () -> registry.create(3636));

        // register handler
        DummyHandler handler = new DummyHandler();
        registry.registerHandler(TestDummyPacket.class, handler);

        // retrieve handler
        PacketHandler<TestDummyPacket, ConnectionCtx> retrieved = registry.getHandle(TestDummyPacket.class);
        assertEquals(handler, retrieved);

        // unhandled packet should throw
        class RandomAssPacket extends NetworkPacket {
            @Override public void write(PacketByteBuf buf) {}
            @Override public void read(PacketByteBuf buf) {}
        }
        assertThrows(UnhandledPacketException.class, () -> registry.getHandle(RandomAssPacket.class));
    }
}
