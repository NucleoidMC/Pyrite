package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record ReferenceRegion(String id) implements Region {
	public static final Codec<ReferenceRegion> CODEC = Codec.STRING.xmap(ReferenceRegion::new, ReferenceRegion::id);

	@Override
	public RegionType<?> getType() {
		return RegionType.POINT;
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		return game.map().region(this.id).contains(game, pos);
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		return game.map().region(this.id).getRandomPoint(game, random);
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return game.map().region(this.id).isInfinite(game);
	}
}
