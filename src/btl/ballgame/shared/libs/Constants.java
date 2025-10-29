package btl.ballgame.shared.libs;

public class Constants {
	public static final int TICKS_PER_SECOND = 30;
	public static final int MS_PER_TICK = (int) (1000.f / TICKS_PER_SECOND);
	public static final int NS_PER_TICK = (int) 1_000_000_000f / TICKS_PER_SECOND;
	
	// meta keys (datawatcher)
	public static final short HP_META_KEY = 0xA0;
	public static final short PADDLE_OWNER_META = 0xA1;
	public static final short BUFF_TYPE_META = 0xB0;
	public static final short BRICK_TINT_META = 0xB1;
	
	// misc constants
	public static final int PADDLE_MOVE_UNITS = 15;
	public static final int PADDLE_MAX_HEALTH = 100;
	public static final int TEAM_STARTING_LIVES = 3;
	public static final int AK_47_MAG_SIZE = 30;
	
	// size consts
	public static final int BRICK_WIDTH = 40;
	public static final int BRICK_HEIGHT = 15;
	
	// enumerates
	public static enum RifleMode {
		SAFE, // khóa an toàn đóng
		SEMI_AUTO, // phát một
		FULL_AUTO; // liên thanh
		// GDQP&AN
		
	    private static final RifleMode[] values = values();
	    public static RifleMode of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	/**
	 * Represents the different phases a match can be in.
	 */
	public static enum MatchPhase {
		MATCH_IDLING, MATCH_ACTIVE, CONCLUDED;
		
	    private static final MatchPhase[] values = values();
	    public static MatchPhase of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	public static enum VoidSide {
		FLOOR, CEILING
	}
	
	public static enum TeamColor {
		RED, BLUE;
		
	    private static final TeamColor[] values = values();
	    public static TeamColor of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	public static enum BuffType {
        PADDLE_EXPAND,
        PIERCING_BALL,
        MULTI_BALL
    }
	
	public static enum ArkanoidMode {
		SOLO_ENDLESS(true),
		
		ONE_VERSUS_ONE(false),
		TWO_VERSUS_TWO(false);
		
		private boolean sp;
		ArkanoidMode(boolean singlePlayer) {
			this.sp = singlePlayer;
		}
		
		public boolean isSinglePlayer() {
			return sp;
		}
		
	    private static final ArkanoidMode[] values = values();
	    public static ArkanoidMode of(int ordinal) {
	        return values[ordinal];
	    }
	}
}
