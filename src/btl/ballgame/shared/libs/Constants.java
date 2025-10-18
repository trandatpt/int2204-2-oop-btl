package btl.ballgame.shared.libs;

public class Constants {
	// meta keys (datawatcher)
	public static final int HP_META_KEY = 0xA;
	public static final int PADDLE_OWNER_MKEY = 0xB01;
	public static final int MISC_META_KEY = 0xFFF;
	
	// misc constants
	public static final int PADDLE_MAX_HEALTH = 100;
	
	// enumerates
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
