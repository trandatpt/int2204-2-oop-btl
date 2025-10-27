package btl.ballgame.shared.libs;

public class Constants {
	// meta keys (datawatcher)
	public static final int HP_META_KEY = 0xA;
	public static final int PADDLE_OWNER_MKEY = 0xB01;
	public static final int MISC_META_KEY = 0xFFF;
	public static final int BUFF_TYPE_META = 36;
	
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
	
	public static enum BuffType {
        PADDLE_EXPAND,
        PIERCING_BALL,
        MULTI_BALL
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
