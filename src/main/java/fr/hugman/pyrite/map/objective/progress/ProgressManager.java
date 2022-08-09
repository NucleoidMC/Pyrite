package fr.hugman.pyrite.map.objective.progress;

import fr.hugman.pyrite.game.PyriteGame;

import java.util.Optional;

public class ProgressManager {
	private final Optional<ScoreProgress> scoreProgress;

	public ProgressManager(PyriteGame game) {
		this.scoreProgress = game.map().objectives().score().map(ScoreProgress::new);
	}

	public Optional<ScoreProgress> scoreProgress() {
		return scoreProgress;
	}
}
