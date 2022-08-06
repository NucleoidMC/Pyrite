package fr.hugman.pyrite.map.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public record RespawnSettings(IntProvider delay) {
	private static final IntProvider DEFAULT_DELAY = ConstantIntProvider.create(5 * 20);
	public static final RespawnSettings DEFAULT = new RespawnSettings(DEFAULT_DELAY);

	public static final Codec<RespawnSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			IntProvider.NON_NEGATIVE_CODEC.fieldOf("delay").orElse(DEFAULT_DELAY).forGetter(RespawnSettings::delay)
	).apply(instance, RespawnSettings::new));

}
