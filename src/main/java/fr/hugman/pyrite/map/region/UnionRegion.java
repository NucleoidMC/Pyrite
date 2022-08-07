package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import xyz.nucleoid.codecs.MoreCodecs;

import java.util.List;

public record UnionRegion(List<Region> regions) implements Region {
	public static final Codec<UnionRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MoreCodecs.listOrUnit(Region.CODEC).fieldOf("regions").forGetter(UnionRegion::regions)
	).apply(instance, UnionRegion::new));

	@Override
	public RegionType<?> getType() {
		return RegionType.UNION;
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		for(Region region : this.regions) {
			if(region.contains(game, pos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		var region = this.regions.get(random.nextInt(this.regions.size()));
		return region.getRandomPoint(game, random);
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		for(Region region : this.regions) {
			if(region.isInfinite(game)) {
				return true;
			}
		}
		return false;
	}
}
