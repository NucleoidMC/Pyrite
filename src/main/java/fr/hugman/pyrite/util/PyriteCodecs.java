package fr.hugman.pyrite.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

import java.util.function.Function;

public class PyriteCodecs {
	public static final Codec<Vec3d> VEC_3D = simpleEither(Vec3d.CODEC, StringParser.VEC_3D_STRING);
	public static final Codec<BlockPos> BLOCK_POS = simpleEither(BlockPos.CODEC, StringParser.BLOCK_POS_STRING);
	public static final Codec<IntProvider> INT_NON_ZERO = nonZeroIntProvider();
	public static final Codec<FloatProvider> FLOAT_NON_ZERO = nonZeroFloatProvider();

	public static <S> Codec<S> simpleEither(Codec<S> baseCodec, Codec<S> simpleCodec) {
		return Codecs.xor(baseCodec, simpleCodec).xmap(e -> e.map(Function.identity(), Function.identity()), Either::right);
	}


	private static Codec<IntProvider> nonZeroIntProvider() {
		Function<IntProvider, DataResult<IntProvider>> function = (provider) -> {
			if (provider.getMin() <= 0 && provider.getMax() >= 0) {
				return DataResult.error("Value provider should not contain the zero value: [" + provider.getMin() + "-" + provider.getMax() + "]");
			}
			return DataResult.success(provider);
		};
		return IntProvider.VALUE_CODEC.flatXmap(function, function);
	}

	private static Codec<FloatProvider> nonZeroFloatProvider() {
		Function<FloatProvider, DataResult<FloatProvider>> function = (provider) -> {
			if (provider.getMin() <= 0.0F && provider.getMax() >= 0.0F) {
				return DataResult.error("Value provider should not contain the zero value: [" + provider.getMin() + "-" + provider.getMax() + "]");
			}
			return DataResult.success(provider);
		};
		return FloatProvider.VALUE_CODEC.flatXmap(function, function);
	}
}
