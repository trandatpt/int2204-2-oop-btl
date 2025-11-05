package btl.ballgame.server.game.match;

import btl.ballgame.shared.libs.Constants.ArkanoidMode;

public class MatchSettings {
	private ArkanoidMode gamemode;
	private int firstToScore;
	private int timePerRound;
	private int teamLives;

	/**
	 * Default constructor with reasonable defaults.
	 */
	public MatchSettings() {
		this.gamemode = ArkanoidMode.ONE_VERSUS_ONE; // in SOLO_ENDLESS, all of the settigns dont matter
		this.firstToScore = 1;
		this.timePerRound = 180; // seconds
		this.teamLives = 2; // 2 hearts before perish
	}

	/**
	 * Settings for an Arkanoid Match
	 *
	 * @param gamemode     the selected game mode
	 * @param firstToScore number of points required to win
	 * @param timePerRound duration of each round in seconds
	 * @param teamLives    number of lives per team, losing all, opponent gets +1 FT score
	 */
	public MatchSettings(ArkanoidMode gamemode, int firstToScore, int timePerRound, int teamLives) {
		this.gamemode = gamemode;
		this.firstToScore = firstToScore;
		this.timePerRound = timePerRound;
		this.teamLives = teamLives;
	}

	public ArkanoidMode getGamemode() {
		return gamemode;
	}

	public int getFirstToScore() {
		return firstToScore;
	}

	public int getTimePerRound() {
		return timePerRound;
	}

	public int getTeamLives() {
		return teamLives;
	}

	public void setGamemode(ArkanoidMode gamemode) {
		this.gamemode = gamemode;
	}

	public void setFirstToScore(int firstToScore) {
		this.firstToScore = firstToScore;
	}

	public void setTimePerRound(int timePerRound) {
		this.timePerRound = timePerRound;
	}

	public void setTeamLives(int teamLives) {
		this.teamLives = teamLives;
	}

	@Override
	public String toString() {
		return "MatchSettings {" +
			"gamemode=" + gamemode +
			", firstToScore=" + firstToScore +
			", timePerRound=" + timePerRound +
			", teamLives=" + teamLives +
		'}';
	}
}
