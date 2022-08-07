package fr.hugman.pyrite.map.region;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import fr.hugman.pyrite.PyriteRegistries;
import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.function.Function;

public interface Region {
	Codec<Either<String, Region>> REGION_CODEC = Codec.either(Codec.STRING, PyriteRegistries.REGION_TYPE.getCodec().dispatch(Region::getType, RegionType::codec));

	Codec<Region> CODEC = REGION_CODEC.xmap(
			either -> either.map(ReferenceRegion::new, Function.identity()),
			provider -> provider instanceof ReferenceRegion reference ? Either.left(reference.regionKey()) : Either.right(provider)
	);

	RegionType<?> getType();

	/**
	 * Test if the region contains the given point
	 */
	boolean contains(PyriteGame game, Vec3d pos);

	/**
	 * Test if the region contains the given point
	 */
	default boolean contains(PyriteGame game, Vec3i pos) {
		return contains(game, Vec3d.of(pos));
	}

	/**
	 * Test if moving from the first point to the second crosses into the region
	 */
	default boolean enters(PyriteGame game, Vec3d from, Vec3d to) {
		return !this.contains(game, from) && this.contains(game, to);
	}

	/**
	 * Test if moving from the first point to the second crosses out of the region
	 */
	default boolean exits(PyriteGame game, Vec3d from, Vec3d to) {
		return this.contains(game, from) && !this.contains(game, to);
	}

	/**
	 * Can this region generate evenly distributed random points?
	 */
	default boolean canGetRandom(PyriteGame game) {
		return !isInfinite(game);
	}

	/**
	 * @param random a random generator to use
	 *
	 * @return a random point within this region.
	 * @throws UnsupportedOperationException if this region cannot generate random points
	 */
	Vec3d getRandomPoint(PyriteGame game, Random random);

	/**
	 * Does this region contain an infinite number of blocks?
	 */
	boolean isInfinite(PyriteGame game);
}

