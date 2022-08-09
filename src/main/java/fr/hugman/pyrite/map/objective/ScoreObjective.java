package fr.hugman.pyrite.map.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record ScoreObjective(Optional<Integer> max, int base, Optional<Integer> min) {
	public static final Codec<ScoreObjective> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("max").forGetter(ScoreObjective::max),
			Codec.INT.fieldOf("base").orElse(0).forGetter(ScoreObjective::base),
			Codec.INT.optionalFieldOf("min").forGetter(ScoreObjective::min)
	).apply(instance, ScoreObjective::new));
}
