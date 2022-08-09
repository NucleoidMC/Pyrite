package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.context.EventEntitySelector;
import fr.hugman.pyrite.map.region.Region;

public record RegionPyritePredicate(Region region, EventEntitySelector entitySelector) implements PyritePredicate {
	public static final Codec<RegionPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Region.CODEC.fieldOf("region").forGetter(RegionPyritePredicate::region),
			EventEntitySelector.CODEC.fieldOf("entity").orElse(EventEntitySelector.BLOCK).forGetter(RegionPyritePredicate::entitySelector)
	).apply(instance, RegionPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.REGION;
	}

	@Override
	public boolean test(EventContext context) {
		return region.contains(context.game(), this.entitySelector.pos(context));
	}
}