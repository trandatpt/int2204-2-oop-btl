package btl.ballgame.server.game.entities;

import btl.ballgame.protocol.packets.out.PacketPlayOutEntityPosition;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.WorldEntity;
import btl.ballgame.shared.libs.Location;

/**
 * Represents a dynamic entity in the game that can be manipulated by
 * an {@link ArkaPlayer} via a client-bound (PlayIn) packet
 */
public abstract class ControllableEntity extends WorldEntity {
	protected ArkaPlayer controller;

	/**
	 * This is a marker class
	 */
	public ControllableEntity(int id, Location location) {
		super(id, location);
	}
	
	public void setController(ArkaPlayer controller) {
		this.controller = controller;
	}
	
	public ArkaPlayer getController() {
		return controller;
	}
	
	@Override
	protected void dispatchLocationUpdate() {
		if (!this.shouldUpdate) return;
		var posPacket = new PacketPlayOutEntityPosition(getId(), this.getLocation());
		controller.playerConnection.sendPacket(posPacket, true);
		for (var player : world.getHandle().getPlayers()) {
			if (controller == player) {
				continue;
			}
			player.playerConnection.sendPacket(posPacket);
		}
		this.shouldUpdate = false;
	}
}
