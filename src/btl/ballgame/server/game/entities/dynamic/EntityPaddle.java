package btl.ballgame.server.game.entities.dynamic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import btl.ballgame.protocol.packets.in.PacketPlayInPaddleControl;
import btl.ballgame.server.ArkaPlayer;
import btl.ballgame.server.game.entities.ControllableEntity;
import btl.ballgame.server.game.match.ArkanoidMatch.PlayerInfo;
import btl.ballgame.shared.libs.AABB;
import btl.ballgame.shared.libs.Constants;

import static btl.ballgame.shared.libs.Constants.*;
import btl.ballgame.shared.libs.Location;
import btl.ballgame.shared.libs.Utils;
import btl.ballgame.shared.libs.Vector2f;
import btl.ballgame.shared.libs.Constants.TeamColor;

public class EntityPaddle extends ControllableEntity {
	static final int MAX_INPUTS_PER_TICK = 10;
	
	// attrib
	private boolean lowerPaddle;
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
		this.team = team;
		setController(p);
		setBoundingBox(PADDLE_WIDTH, PADDLE_HEIGHT);
	}
	
	public TeamColor getTeam() {
		return team;
	}
	
	public ArkaPlayer getPlayer() {
		return controller;
	}
	
	/**
	 * Called on packet receive, since this game is tick-based,
	 * process on next server/world tick
	 * 
	 * @param packet
	 */
	public void enqueueControl(PacketPlayInPaddleControl packet) {
		if (!packet.isLeft() && !packet.isRight() && !packet.isShoot()) {
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
		this.move(PADDLE_MOVE_UNITS);
	}
	
	public void moveLeft() {
		this.move(-PADDLE_MOVE_UNITS);
	}
	
	private int consecutiveShots = 0;
	private long lastShot = 0;
	private long lastMoved = 0;

	public void shootBullet() {
		PlayerInfo p = controller.getCurrentGame().getPlayerInfoOf(controller);
		
		// khoa an toan dang dong or ran out of ammo
		if (p.getFiringMode() == RifleMode.SAFE || p.getRifleAmmo() <= 0) {
			return;
		}
		
		Location initial = getLocation();
		// if the player is the lower paddle, the bullet flies up
		Vector2f direction = isLowerPaddle() ? 
			new Vector2f(0, -1) : 
			new Vector2f(0, 1)
		;
		
		float spraySpread = 0.0f;
		// if the player moved in the last 100ms, add spread
		if (System.currentTimeMillis() - lastMoved < 100) {
			// drift by +/-0.36
			spraySpread = (world.random.nextFloat() - 0.5f) * 0.72f;
		}
		
		// penalty for consecutive fires (spray)
		// if the last shot is over 300ms ago, reset penalty (Spread)
		if (System.currentTimeMillis() - lastShot >= 300) {
			consecutiveShots = 0;
		}
		
		// spread for up to 1 unit after 10 consecutive shots
		float sprayFactor = Math.min(consecutiveShots / 10f, 1f); // ramps to 1.0 after 10 shots
		spraySpread += (world.random.nextFloat() - 0.5f) * 1f * sprayFactor;
		
		direction.setX(spraySpread);
		initial.setDirection(direction);
		initial.add(direction.clone().normalize().multiply(Constants.PADDLE_HEIGHT + 10));
		p.fireRounds(1);
		
		// spawn the bullet
		EntityAKBullet bullet = new EntityAKBullet(
			world.nextEntityId(), initial
		);
		bullet.setOwner(controller);
		world.runNextTick(() -> world.addEntity(bullet));
		
		// for penalty/spray calculation
		consecutiveShots++;
		lastShot = System.currentTimeMillis();
	}
	
	@Override
	public void onSpawn() {
		if (controller == null) return;
		// assign the player UUID LSB to this entity (to save some BITS!)
		this.dataWatcher.watch(PADDLE_OWNER_META,
			controller.getUniqueId().getLeastSignificantBits()
		);
		this.dataWatcher.watch(PADDLE_EXPANDED_META, false);
		this.dataWatcher.watch(RENDER_UPSIDEDOWN_META, !lowerPaddle); // upper paddle = render upside down
	}
		
	@Override
	protected void tick() {
		int processed = 0;
		// prevent malicious clients from spamming
		while (!moveQueue.isEmpty() && processed < MAX_INPUTS_PER_TICK) {
			PacketPlayInPaddleControl packet = moveQueue.poll();
			if (packet.isLeft()) {
				moveLeft();
				lastMoved = System.currentTimeMillis();
			}
			if (packet.isRight()) {
				moveRight();
				lastMoved = System.currentTimeMillis();
			}
			if (packet.isShoot() && (System.currentTimeMillis() - lastShot) > 250) {
				shootBullet();
			}
			++processed;
		}
	}
}