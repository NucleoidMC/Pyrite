package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.context.EventPositionSelector;
import fr.hugman.pyrite.map.region.Region;

public record RegionPyritePredicate(Region region, EventPositionSelector positionSelector) implements PyritePredicate {
	public static final Codec<RegionPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Region.CODEC.fieldOf("region").forGetter(RegionPyritePredicate::region),
			EventPositionSelector.CODEC.fieldOf("position").forGetter(RegionPyritePredicate::positionSelector)
	).apply(instance, RegionPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.REGION;
	}

	@Override
	public boolean test(EventContext context) {
		return region.contains(context.game(), this.positionSelector.get(context));
	}
}