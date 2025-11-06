package btl.ballgame.tests.client;

import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import btl.ballgame.client.ClientPlayer;

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