package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record BlockRegion(BlockPos pos) implements Region {
	public static final Codec<BlockRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteCodecs.BLOCK_POS.fieldOf("pos").forGetter(BlockRegion::pos)
	).apply(instance, BlockRegion::new));

	@Override
	public RegionType<?> getType() {
		return RegionType.BLOCK;
	}

	@Override
	public Vec3d getRandomPoint(PyriteMap map, Random random) {
		return Vec3d.of(this.pos.add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
	}

	@Override
	public boolean contains(PyriteMap map, Vec3d pos) {
		return new BlockPos(pos).equals(this.pos);
	}

	@Override
	public boolean isInfinite(PyriteMap map) {
		return false;
	}
}
