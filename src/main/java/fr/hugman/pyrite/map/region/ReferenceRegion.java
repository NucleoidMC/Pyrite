package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record ReferenceRegion(String regionKey) implements Region {
	public static final Codec<ReferenceRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("region").forGetter(ReferenceRegion::regionKey)
	).apply(instance, ReferenceRegion::new));

	@Override
	public RegionType<?> getType() {
		return RegionType.REFERENCE;
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		return game.map().region(this.regionKey).contains(game, pos);
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		return game.map().region(this.regionKey).getRandomPoint(game, random);
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return game.map().region(this.regionKey).isInfinite(game);
	}
}
