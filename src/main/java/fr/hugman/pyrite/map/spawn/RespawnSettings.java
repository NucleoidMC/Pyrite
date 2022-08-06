package fr.hugman.pyrite.map.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;

public record RespawnSettings(IntProvider delay) {
	public static final Codec<RespawnSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntProvider.NON_NEGATIVE_CODEC.fieldOf("delay").forGetter(RespawnSettings::delay)
	).apply(instance, RespawnSettings::new));
}
