package fr.hugman.pyrite.map.point;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.registry.PyriteRegistries;
import net.minecraft.registry.Registry;

public record PointProviderType<P extends PointProvider>(Codec<P> codec) {
	public static final PointProviderType<FixedPointProvider> FIXED = register("fixed", FixedPointProvider.CODEC);

	private static <P extends PointProvider> PointProviderType<P> register(String name, Codec<P> codec) {
		return Registry.register(PyriteRegistries.POINT_PROVIDER_TYPE, Pyrite.id(name), new PointProviderType<>(codec));
	}

	@Override
	public String toString() {
		var id = PyriteRegistries.POINT_PROVIDER_TYPE.getId(this);
		if(id == null) {
			return "UNKNOWN";
		}
		return id.toString();
	}
}
