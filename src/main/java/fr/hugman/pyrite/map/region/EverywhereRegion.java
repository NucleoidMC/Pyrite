package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record EverywhereRegion() implements Region {
	public static final EverywhereRegion INSTANCE = new EverywhereRegion();

	public static final Codec<EverywhereRegion> CODEC = Codec.unit(INSTANCE);

	@Override
	public RegionType<?> getType() {
		return RegionType.EVERYWHERE;
	}

	@Override
	public Vec3d getRandomPoint(PyriteMap map, Random random) {
		throw new UnsupportedOperationException(Text.translatable("error.pyrite.region.random_point_unsupported", this.getType()).toString());
	}

	@Override
	public boolean contains(PyriteMap map, Vec3d pos) {
		return true;
	}

	@Override
	public boolean isInfinite(PyriteMap map) {
		return true;
	}
}
