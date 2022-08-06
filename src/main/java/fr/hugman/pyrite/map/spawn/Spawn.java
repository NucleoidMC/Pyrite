package fr.hugman.pyrite.map.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.point.PointProvider;
import fr.hugman.pyrite.util.Angle;

import java.util.Optional;

public record Spawn(PointProvider point, Angle angle, Optional<String> kit) {
	public static final Codec<Spawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PointProvider.CODEC.fieldOf("point").forGetter(Spawn::point),
			Angle.CODEC.fieldOf("angle").orElse(Angle.DEFAULT).forGetter(Spawn::angle),
			Codec.STRING.optionalFieldOf("kit").forGetter(Spawn::kit)
	).apply(instance, Spawn::new));
}
