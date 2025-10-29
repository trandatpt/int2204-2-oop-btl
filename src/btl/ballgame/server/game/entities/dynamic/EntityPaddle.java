package btl.ballgame.server.game.entities.dynamic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.entities.ControllableEntity;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Constants;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Utils;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class EntityPaddle extends ControllableEntity {
	static final int MAX_INPUTS_PER_TICK = 15;
	
	// attrib
	private boolean lowerPaddle;
	private ArkaPlayer player;
	private TeamColor team;
	
	// the queue!
	private Queue<PacketPlayInPaddleControl> moveQueue = new ConcurrentLinkedQueue<>();
	
	/**
	 * Constructs a new Paddle entity at the specified location.
	 *
	 * @param id       Entity ID.
	 * @param location Initial world location.
	 * @param p 	   The player who owns it
	 * @param team     The team of the player who owns it (wtf)
	 */
	public EntityPaddle(
		int id, Location location, // base info
		ArkaPlayer p, TeamColor team // extras
	) {
		super(id, location);
		this.player = p;
		this.team = team;
		setBoundingBox(88, 16);
	}
	
	public TeamColor getTeam() {
		return team;
	}
	
	/**
	 * Called on packet receive, since this game is tick-based,
	 * process on next server/world tick
	 * 
	 * @param packet
	 */
	public void enqueueMove(PacketPlayInPaddleControl packet) {
		if (!packet.isLeft() && !packet.isRight()) {
			return;
		}
		moveQueue.add(packet);
	}
	
	/**
	 * Trust me this is important
	 * @param lowerPaddle
	 */
	public void setLowerPaddle(boolean lowerPaddle) {
		this.lowerPaddle = lowerPaddle;
	}
	
	public boolean isLowerPaddle() {
		return lowerPaddle;
	}
	
	/**
	 * Move the paddle in the X axis
	 * @param relX relative x
	 */
	private void move(int relX) {
		AABB bb = getBoundingBox();
		int halfWidth = (int) (bb.getWidth()) >> 1;
	    int maxX = world.getWidth() - halfWidth;
	    // centered entity system
	    int newX = Utils.clamp(getLocation().getX() + relX, halfWidth, maxX);
	    if (newX != getLocation().getX()) {
	        teleport(getLocation().setX(newX));
	    }
	}


	public void moveRight() {
		this.move(Constants.PADDLE_MOVE_UNITS);
	}
	
	public void moveLeft() {
		this.move(-Constants.PADDLE_MOVE_UNITS);
	}
	
	@Override
	public void onSpawn() {
		if (player == null) return;
		// assign the player UUID LSB to this entity (to save some BITS!)
		this.dataWatcher.watch(
			Constants.PADDLE_OWNER_META,
			player.getUniqueId().getLeastSignificantBits()
		);
	}
	
	@Override
	protected void tick() {
		int processed = 0;
		// prevent malicious clients from spamming
		while (!moveQueue.isEmpty() && processed < MAX_INPUTS_PER_TICK) {
			PacketPlayInPaddleControl packet = moveQueue.poll();
			if (packet.isLeft()) {
				moveLeft();
			}
			if (packet.isRight()) {
				moveRight();
			}
			++processed;
		}
	}
}