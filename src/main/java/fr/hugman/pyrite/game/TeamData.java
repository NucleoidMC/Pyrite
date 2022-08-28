package fr.hugman.pyrite.game;

public class TeamData {
	public boolean isWinning;
	public boolean eliminated;

	public static TeamData create() {
		return new TeamData();
	}

}
