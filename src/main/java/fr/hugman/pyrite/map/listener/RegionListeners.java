package fr.hugman.pyrite.map.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record RegionListeners(
		String regionKey,
		Optional<PyriteListener> enter,
		Optional<PyriteListener> leave,
		Optional<PyriteListener> place,
		Optional<PyriteListener> brek,
		Optional<PyriteListener> use
) {
	public static final Codec<RegionListeners> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("region").forGetter(RegionListeners::regionKey),
			PyriteListener.CODEC.optionalFieldOf("enter").forGetter(RegionListeners::enter),
			PyriteListener.CODEC.optionalFieldOf("leave").forGetter(RegionListeners::leave),
			PyriteListener.CODEC.optionalFieldOf("place").forGetter(RegionListeners::place),
			PyriteListener.CODEC.optionalFieldOf("break").forGetter(RegionListeners::brek),
			PyriteListener.CODEC.optionalFieldOf("use").forGetter(RegionListeners::use)
	).apply(instance, RegionListeners::new));

	public static final Codec<List<RegionListeners>> LIST_CODEC = CODEC.listOf();
}
