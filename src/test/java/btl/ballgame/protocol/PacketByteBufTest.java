package btl.ballgame.protocol;

import static org.junit.Assert.*;
import org.junit.Test;
import java.nio.BufferOverflowException;

public class PacketByteBufTest {

    @Test
    public void testBufferOverflowFixed() {
        PacketByteBuf buf = PacketByteBuf.mallocFixed(2);

        try {
            buf.writeInt32(123);
            fail("Expected BufferOverflowException to be thrown, but no exception was thrown.");
        } catch (BufferOverflowException e) {
        } catch (Exception e) {
            fail("Expected BufferOverflowException, but got: " + e.getClass().getSimpleName());
        }
    }
}
