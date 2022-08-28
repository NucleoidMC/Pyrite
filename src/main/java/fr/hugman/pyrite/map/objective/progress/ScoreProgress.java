package fr.hugman.pyrite.map.objective.progress;

import eu.pb4.sidebars.api.lines.LineBuilder;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.map.objective.ScoreObjective;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScoreProgress {
	private final ScoreObjective objective;
	private final Map<GameTeamKey, Integer> points = new HashMap<>();

	public ScoreProgress(ScoreObjective objective) {
		this.objective = objective;
	}

	public int points(GameTeamKey key) {
		return this.points.computeIfAbsent(key, teamKey -> this.objective.base());
	}

	public void addPoint(GameTeamKey key, int amount) {
		this.points.compute(key, (teamKey, current) -> current + amount);
	}

	public boolean isWinning(GameTeamKey teamKey) {
		return this.points.get(teamKey) >= this.objective.max().orElse(this.objective.base());
	}

	public boolean shouldBeEliminated(GameTeamKey teamKey) {
		return this.points.get(teamKey) <= this.objective.max().map(integer -> 0).orElse(1);
	}

	public GameTeamKey getTeamKey(int i) {
		return this.points.keySet().toArray(new GameTeamKey[0])[i];
	}

	public GameTeamKey getTeamKey(int i, GameTeamKey except) {
		boolean includesExcept = false;
		for(int j = 0; j <= i; j++) {
			GameTeamKey key = this.getTeamKey(j);
			if(key == except) {
				includesExcept = true;
			}
		}
		return getTeamKey(includesExcept ? i + 1 : i);
	}

	public Text getTeamText(PyriteGame game, GameTeamKey key) {
		return Text.literal(game.map().team(key).config().name().getString() + ": " + points.get(key));
	}

	public void fillSidebar(PyriteGame game, LineBuilder builder) {
		// display the progress of the player's team first
		builder.add(player -> {
			var teamKey = game.playerManager().teamKey(player);

			if(teamKey == null) teamKey = getTeamKey(0);

			return getTeamText(game, teamKey);
		});

		// then display the other teams
		if(this.points.keySet().size() > 1) {
			for(int i = 0 ; i < this.points.keySet().size() - 1; i++) {
				int finalI = i;
				builder.add(player -> {
					var teamKey = game.playerManager().teamKey(player);

					if(teamKey == null) teamKey = getTeamKey(finalI + 1);
					else teamKey = getTeamKey(finalI, teamKey);

					return getTeamText(game, teamKey);
				});
			}
		}
	}
}
