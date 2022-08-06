package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.map.PyriteMap;
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
	public boolean contains(PyriteMap map, Vec3d pos) {
		return pos.equals(this.point());
	}

	@Override
	public Vec3d getRandomPoint(PyriteMap map, Random random) {
		return point;
	}

	@Override
	public boolean isInfinite(PyriteMap map) {
		return false;
	}
}
