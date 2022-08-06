package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record FixedPointRegion(Vec3d point) implements Region {
	public static final Codec<FixedPointRegion> CODEC = PyriteCodecs.VEC_3D.xmap(FixedPointRegion::new, FixedPointRegion::point);

	@Override
	public RegionType<?> getType() {
		return RegionType.POINT;
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		return pos.equals(this.point());
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		return point;
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return false;
	}
}
