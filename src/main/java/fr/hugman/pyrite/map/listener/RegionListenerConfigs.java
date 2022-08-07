package fr.hugman.pyrite.map.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record RegionListenerConfigs(
		String regionKey,
		Optional<PyriteListener> enter,
		Optional<PyriteListener> exit,
		Optional<PyriteListener> place,
		Optional<PyriteListener> brek, //funny silly name ^-^
		Optional<PyriteListener> use
) {
	public static final Codec<RegionListenerConfigs> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("region").forGetter(RegionListenerConfigs::regionKey),
			PyriteListener.CODEC.optionalFieldOf("enter").forGetter(RegionListenerConfigs::enter),
			PyriteListener.CODEC.optionalFieldOf("exit").forGetter(RegionListenerConfigs::exit),
			PyriteListener.CODEC.optionalFieldOf("place").forGetter(RegionListenerConfigs::place),
			PyriteListener.CODEC.optionalFieldOf("break").forGetter(RegionListenerConfigs::brek),
			PyriteListener.CODEC.optionalFieldOf("use").forGetter(RegionListenerConfigs::use)
	).apply(instance, RegionListenerConfigs::new));

	public static final Codec<List<RegionListenerConfigs>> LIST_CODEC = CODEC.listOf();
}
