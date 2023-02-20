package fr.hugman.pyrite.game;

public class TeamData {
	public boolean won;
	public boolean eliminated;

	public static TeamData create() {
		return new TeamData();
	}

}
