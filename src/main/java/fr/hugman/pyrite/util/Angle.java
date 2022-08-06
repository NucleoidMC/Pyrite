package fr.hugman.pyrite.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record Angle(FloatProvider yaw, FloatProvider pitch) {
	private static final FloatProvider DEFAULT_YAW = ConstantFloatProvider.create(0.0F);
	private static final FloatProvider DEFAULT_PITCH = ConstantFloatProvider.create(0.0F);

	public static final MapCodec<Angle> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			FloatProvider.VALUE_CODEC.fieldOf("yaw").orElse(DEFAULT_YAW).forGetter(o -> o.yaw),
			FloatProvider.VALUE_CODEC.fieldOf("pitch").orElse(DEFAULT_PITCH).forGetter(o -> o.pitch)
	).apply(instance, Angle::new));

	public static final Codec<Angle> CODEC = MAP_CODEC.codec();

	public static final Angle DEFAULT = new Angle(DEFAULT_YAW, DEFAULT_PITCH);
}
