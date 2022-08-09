package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.game.PyriteGame;
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
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		return Vec3d.of(this.pos.add(random.nextDouble(), random.nextDouble(), random.nextDouble()));
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		return new BlockPos(pos).equals(this.pos);
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return false;
	}
}
