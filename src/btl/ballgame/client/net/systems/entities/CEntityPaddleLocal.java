package btl.ballgame.client.net.systems.entities;

import btl.ballgame.client.ArkanoidGame;
import btl.ballgame.client.ClientPlayer;
import btl.ballgame.client.net.systems.ITickableCEntity;
import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.DataWatcher;

import static btl.ballgame.shared.libs.Utils.*;

public class CEntityPaddleLocal extends CEntityPaddle implements ITickableCEntity {
	private boolean moveLeft = false, moveRight = false;
	// this is the speculative position of the client
	private int clientX;
	private boolean canMove = true; // this is set by the server
	
	@Override
	public int getRenderX() {
		// this could get choppy, so lerp the shit out of it
		return intLerp(oldX, clientX, getAlpha(lastTickNano)) - (getWidth() >> 1);
	}
	
	private int oldX;
	private long lastTickNano;
	@Override
	public void onTick() {
		if (!this.canMove || (!moveLeft && !moveRight)) {
			return; // prevent from wasting data
		}
		// setting up lerp
		this.oldX = this.clientX;
		this.lastTickNano = System.nanoTime();
		// if the user pressed L or R this tick, move accordingly 
		if (moveLeft) move(-Constants.PADDLE_MOVE_UNITS);
		if (moveRight) move(Constants.PADDLE_MOVE_UNITS);
		ArkanoidGame.core().getConnection().sendPacket(
			new PacketPlayInPaddleControl(moveLeft, moveRight)
		);
	}
	
	/**
	 * Move the paddle (on the client side) in the X axis
	 * This does not affect the server location, at all
	 * 
	 * @param relX relative x
	 */
	private void move(int relX) {
		AABB bb = getBoundingBox();
		int halfWidth = (int) (bb.getWidth()) >> 1;
		int maxX = this.getWorld().getWidth() - halfWidth;
		clientX = clamp(clientX + relX, halfWidth, maxX);
	}
	
	@Override
	public void onEntitySpawn() {
		// set the initial position
		this.clientX = getServerLocation().getX();
	}
	
	@Override
	public void onAfterLocationUpdate() {
		// correct the client's speculative position if it
		// drifted too far (> 30 units)
		int serverX = getMutableServerLocation().getX();
		if (Math.abs(serverX - clientX) > (Constants.PADDLE_MOVE_UNITS << 1)) {
			// setting up lerp
			this.oldX = this.clientX;
			this.lastTickNano = System.nanoTime();
			this.clientX = serverX;
		}
	}
	
	public void setMoveLeft(boolean moveLeft) {
		this.moveLeft = moveLeft;
	}
	
	public void setMoveRight(boolean moveRight) {
		this.moveRight = moveRight;
	}
	
	public void setCanMoveStatus(boolean canMove) {
		this.canMove = canMove;
		// sync the position immediately
		this.clientX = getServerLocation().getX();
	}
	
	/**
	 * Checks if the given {@link DataWatcher} OF A PADDLE! 
	 * is owned by the current client.
	 *
	 * @param watcher the {@link DataWatcher} OF A PADDLE to check
	 * @return {@code true} if owned by this client
	 */
	public static boolean isOwnedByThisClient(DataWatcher watcher) {
		ClientPlayer p = ArkanoidGame.core().getPlayer();
		return p.getUniqueId().getLeastSignificantBits() 
			== (long) watcher.get(Constants.PADDLE_OWNER_META)
		;
	}
}
