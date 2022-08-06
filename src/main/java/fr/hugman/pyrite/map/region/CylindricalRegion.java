package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.util.PyriteCodecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public record CylindricalRegion(Vec3d base, double radius, double top) implements Region {
	public static final Codec<CylindricalRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteCodecs.VEC_3D.fieldOf("base").forGetter(CylindricalRegion::base),
			Codec.DOUBLE.fieldOf("radius").forGetter(CylindricalRegion::radius),
			Codec.DOUBLE.fieldOf("top").forGetter(CylindricalRegion::top)
	).apply(instance, CylindricalRegion::new));

	@Override
	public RegionType<?> getType() {
		return RegionType.CYLINDER;
	}

	@Override
	public Vec3d getRandomPoint(PyriteGame game, Random random) {
		double angle = random.nextDouble() * Math.PI * 2;
		double hyp = random.nextDouble() + random.nextDouble();
		hyp = (hyp < 1 ? hyp : 2 - hyp) * radius;
		double x = Math.cos(angle) * hyp + base.getX();
		double z = Math.sin(angle) * hyp + base.getZ();
		double y = base.getY() + random.nextDouble() * (top - base.getY());
		return new Vec3d(x, y, z);
	}

	@Override
	public boolean contains(PyriteGame game, Vec3d pos) {
		if(pos.getY() < base.getY()) return false; // point is below
		if(pos.getY() > base.getY() + top) return false; // point is above

		double d = this.base().getX() - pos.x;
		double f = this.base().getZ() - pos.z;
		return !(d * d + f * f > radius * radius); // point is outside the base circle
	}

	@Override
	public boolean isInfinite(PyriteGame game) {
		return false;
	}
}
