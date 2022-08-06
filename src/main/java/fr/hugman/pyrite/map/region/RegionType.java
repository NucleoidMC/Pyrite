package fr.hugman.pyrite.map.region;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.PyriteRegistries;
import net.minecraft.util.registry.Registry;

public record RegionType<P extends Region>(Codec<P> codec) {
	public static final RegionType<FixedPointRegion> POINT = register("point", FixedPointRegion.CODEC);
	public static final RegionType<BlockRegion> BLOCK = register("block", BlockRegion.CODEC);
	public static final RegionType<CuboidRegion> CUBOID = register("cuboid", CuboidRegion.CODEC);
	public static final RegionType<CylindricalRegion> CYLINDER = register("cylinder", CylindricalRegion.CODEC);
	public static final RegionType<EverywhereRegion> EVERYWHERE = register("everywhere", EverywhereRegion.CODEC);

	private static <P extends Region> RegionType<P> register(String name, Codec<P> codec) {
		return Registry.register(PyriteRegistries.REGION_TYPE, Pyrite.id(name), new RegionType<>(codec));
	}

	@Override
	public String toString() {
		var id = PyriteRegistries.REGION_TYPE.getId(this);
		if(id == null) {
			return "UNKNOWN";
		}
		return id.toString();
	}
}
