package btl.ballgame.shared.libs;

public class Constants {
	public static final int TICKS_PER_SECOND = 30;
	public static final int MS_PER_TICK = (int) (1000.f / TICKS_PER_SECOND);
	public static final int NS_PER_TICK = (int) 1_000_000_000f / TICKS_PER_SECOND;
	
	// meta keys (datawatcher)
	public static final int HP_META_KEY = 0xA;
	public static final int PADDLE_OWNER_MKEY = 0xB01;
	public static final int MISC_META_KEY = 0xFFF;
	
	// misc constants
	public static final int PADDLE_MAX_HEALTH = 100;
	public static final int TEAM_STARTING_LIVES = 3;
	
	// enumerates
	/**
	 * Represents the different phases a match can be in.
	 */
	public enum MatchPhase {
		MATCH_IDLING, BRICK_WARFARE, AK47_MODE, CONCLUDED
	}
	
	public static enum VoidSide {
		FLOOR, CEILING
	}
	
	public static enum TeamColor {
		RED, BLUE
	}
	
	public static enum ArkanoidMode {
		SOLO_ENDLESS(true),
		
		ONE_VERSUS_ONE(false),
		TWO_VERSUS_TWO(false),
		
		BOSS_RAID(false); // TODO
		
		private boolean sp;
		ArkanoidMode(boolean singlePlayer) {
			this.sp = singlePlayer;
		}
		
		public boolean isSinglePlayer() {
			return sp;
		}
	}
}
