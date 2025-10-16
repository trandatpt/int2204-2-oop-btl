package btl.ballgame.server.game.match;

public enum ArkanoidMode {
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
