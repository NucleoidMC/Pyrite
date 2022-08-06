package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.PyriteRegistries;
import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public interface Region {
	Codec<Region> CODEC = PyriteRegistries.REGION_TYPE.getCodec().dispatch(Region::getType, RegionType::codec);

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

