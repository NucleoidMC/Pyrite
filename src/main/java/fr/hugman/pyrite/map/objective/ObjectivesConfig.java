package fr.hugman.pyrite.map.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.PyriteMap;

import java.util.Optional;

public record ObjectivesConfig(Optional<ScoreObjective> score) {
	public static final Codec<ObjectivesConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ScoreObjective.CODEC.optionalFieldOf("score").forGetter(ObjectivesConfig::score)
	).apply(instance, ObjectivesConfig::new));

	public ObjectivesConfig {
		// if no objectives are defined, throw an exception
		if(score.isEmpty()) {
			throw new IllegalStateException("No objectives found");
		}
	}
}
