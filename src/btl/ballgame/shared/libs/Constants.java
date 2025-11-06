package btl.ballgame.shared.libs;

public class Constants {
	public static final int TICKS_PER_SECOND = 30;
	public static final int MS_PER_TICK = (int) (1000.f / TICKS_PER_SECOND);
	public static final int NS_PER_TICK = (int) 1_000_000_000f / TICKS_PER_SECOND;
	
	// meta keys (datawatcher)
	public static final short MAX_HP_META_KEY = 0xA0;
	public static final short HP_META_KEY = 0xA1;
	public static final short PADDLE_OWNER_META = 0xA2;
	public static final short PADDLE_EXPANDED_META = 0xA3;
	public static final short RENDER_UPSIDEDOWN_META = 0xF4;
	public static final short ITEM_TYPE_META = 0xB0;
	public static final short BRICK_TINT_META = 0xB1;
	public static final short BALL_PRIMARY_META = 0xB2;
	public static final short BALL_ENLARGED_META = 0xB3;
	public static final short EXPLOSIVE_PRIMED_META = 0xC0;
	
	// misc constants
	public static final int PADDLE_MOVE_UNITS = 15;
	public static final int PADDLE_MAX_HEALTH = 100;
	public static final int TEAM_STARTING_LIVES = 3;
	public static final int AK_47_MAG_SIZE = 30;
	
	// size consts
	public static final int BRICK_WIDTH = 60, BRICK_HEIGHT = 20;
	public static final int PADDLE_WIDTH = 88, PADDLE_HEIGHT = 16;
	
	// enumerates
	public static enum RifleMode {
		SAFE("Safe"), // khóa an toàn đóng
		SEMI_AUTO("Semi-Auto"), // phát một
		FULL_AUTO("Full-Auto"); // liên thanh
		// GDQP&AN
		
		private String friendlyName;
		private RifleMode(String name) {
			this.friendlyName = name;
		}
		
		public String getFriendlyName() {
			return friendlyName;
		}
		
	    private static final RifleMode[] values = values();
	    public static RifleMode of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	public static enum ParticleType {
		SPRITE, 
		OVAL, 
		RECTANGLE
	}
	
	public static enum GameOverType {
		SOLO_GAME_END,
		VERSUS_VICTORY,
		VERSUS_DEFEAT
	}
	
	public static enum EnumTitle {
		PRETITLE, 
		TITLE,
		SUBTITLE
	}
	
	public static record UPlayerEffect(EffectType effect, long endTime) {};
	
	public static enum DriftBehavior {
		NONE(false, false, false), 
		ROTATING_WHILE_DRIFTING(true, false, false), 
		SHRINK_WHILE_DRIFTING(false, true, false),
		GROW_WHILE_DRIFTING(false, false, true),
		ROTATE_AND_SHRINK(true, true, false),
		ROTATE_AND_GROW(true, false, true);
		
	    public final boolean rotates;
	    public final boolean shrinks;
	    public final boolean grows;

	    DriftBehavior(boolean rotates, boolean shrinks, boolean grows) {
	        this.rotates = rotates;
	        this.shrinks = shrinks;
	        this.grows = grows;
	    }
	}
	
	public static enum ParticlePriority {
		BEFORE_ENTITIES,
		AFTER_ENTITIES,
		LATEST_IGNORE_FLIP,
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
	
	public static enum EffectType {
        PADDLE_EXPAND,
        ENLARGED_BALL,
        MULTI_BALL;
        
	    private static final EffectType[] values = values();
	    public static EffectType of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	public static enum ItemType {
		AK47_AMMO,
		RANDOM_EFFECT,
		HEART;
		
	    private static final ItemType[] values = values();
	    public static ItemType of(int ordinal) {
	        return values[ordinal];
	    }
	}
	
	public static enum ArkanoidMode {
		SOLO_ENDLESS("Classic Endless Arkanoid", true),
		
		ONE_VERSUS_ONE("One vs One (1v1)",false),
		TWO_VERSUS_TWO("Two vs Two (2v2)", false);
		
		private boolean sp;
		private String friendlyName;
		ArkanoidMode(String friendlyName, boolean singlePlayer) {
			this.sp = singlePlayer;
			this.friendlyName = friendlyName;
		}
		
		public boolean isSinglePlayer() {
			return sp;
		}
		
		public String getFriendlyName() {
			return friendlyName;
		}
		
	    private static final ArkanoidMode[] values = values();
	    public static ArkanoidMode of(int ordinal) {
	        return values[ordinal];
	    }
	}
}
