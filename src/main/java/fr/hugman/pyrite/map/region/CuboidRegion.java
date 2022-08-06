package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record CuboidRegion(Vec3d min, Vec3d max) implements Region {
	public static final Codec<CuboidRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteCodecs.VEC_3D.fieldOf("a").forGetter(CuboidRegion::min),
			PyriteCodecs.VEC_3D.fieldOf("b").forGetter(CuboidRegion::max)
	).apply(instance, CuboidRegion::of));

	public static CuboidRegion of(Vec3d a, Vec3d b) {
		Vec3d min = new Vec3d(
				Math.min(a.getX(), b.getX()),
				Math.min(a.getY(), b.getY()),
				Math.min(a.getZ(), b.getZ()));
		Vec3d max = new Vec3d(
				Math.max(a.getX(), b.getX()),
				Math.max(a.getY(), b.getY()),
				Math.max(a.getZ(), b.getZ()));
		return new CuboidRegion(min, max);
	}

	@Override
	public RegionType<?> getType() {
		return RegionType.CUBOID;
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		double x = this.randomRange(random, this.min.getX(), this.max.getX());
		double y = this.randomRange(random, this.min.getY(), this.max.getY());
		double z = this.randomRange(random, this.min.getZ(), this.max.getZ());
		return new Vec3d(x, y, z);
	}

	private double randomRange(Random random, double min, double max) {
		return (max - min) * random.nextDouble() + min;
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		return pos.getX() >= this.min.getX()
				&& pos.getY() >= this.min.getY()
				&& pos.getZ() >= this.min.getZ()
				&& pos.getX() <= this.max.getX()
				&& pos.getY() <= this.max.getY()
				&& pos.getZ() <= this.max.getZ();
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return false;
	}
}
