package fr.hugman.pyrite.map.point;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import fr.hugman.pyrite.registry.PyriteRegistries;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Function;

public interface PointProvider {
	Codec<Either<Vec3d, PointProvider>> POINT_CODEC = Codec.either(PyriteCodecs.VEC_3D, PyriteRegistries.POINT_PROVIDER_TYPE.getCodec().dispatch(PointProvider::getType, PointProviderType::codec));
	Codec<PointProvider> CODEC = POINT_CODEC.xmap(
			either -> either.map(FixedPointProvider::new, Function.identity()),
			provider -> provider instanceof FixedPointProvider fixed ? Either.left(fixed.point()) : Either.right(provider)
	);

	PointProviderType<?> getType();

	Vec3d getRandom(Random random, @Nullable EventContext context);

	default Vec3d getRandom(Random random) {
		return getRandom(random, null);
	}

	Iterator<Vec3d> iterator(@Nullable EventContext context);

	default Iterator<Vec3d> iterator() {
		return iterator(null);
	}
}