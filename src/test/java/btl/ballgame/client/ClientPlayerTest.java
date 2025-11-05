package btl.ballgame.client;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.UUID;

public class ClientPlayerTest {

    @Test
    public void testGetUserName() {
        UUID id = UUID.randomUUID();
        ClientPlayer cp = new ClientPlayer("Dat", id);

        assertEquals("Dat", cp.getUserName());
    }

    @Test
    public void testGetUniqueId() {
        UUID id = UUID.randomUUID();
        ClientPlayer cp = new ClientPlayer("User123", id);

        assertEquals(id, cp.getUniqueId());
    }
}
