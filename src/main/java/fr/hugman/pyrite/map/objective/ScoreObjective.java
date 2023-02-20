package fr.hugman.pyrite.map.objective;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ScoreObjective(int startValue, int min, int max) {
	public static final Codec<ScoreObjective> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("start_value", 0).forGetter(ScoreObjective::startValue),
			Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(ScoreObjective::min),
			Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(ScoreObjective::max)
	).apply(instance, ScoreObjective::new));
}
