package fr.hugman.pyrite.map.point;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;

public record FixedPointProvider(Vec3d point) implements PointProvider {
	public static final Codec<FixedPointProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteCodecs.VEC_3D.fieldOf("point").forGetter(FixedPointProvider::point)
	).apply(instance, FixedPointProvider::new));

	@Override
	public PointProviderType<?> getType() {
		return PointProviderType.FIXED;
	}

	@Override
	public Vec3d getRandom(Random random, @Nullable EventContext context) {
		return point;
	}

	@NotNull
	@Override
	public Iterator<Vec3d> iterator(@Nullable EventContext context) {
		return Collections.singleton(point).iterator();
	}
}