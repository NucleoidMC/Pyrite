package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.context.EventContext;

public record AlwaysPyritePredicate() implements PyritePredicate {
	public static final AlwaysPyritePredicate INSTANCE = new AlwaysPyritePredicate();

	public static final Codec<AlwaysPyritePredicate> CODEC = Codec.unit(INSTANCE);

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.ALWAYS;
	}

	@Override
	public boolean test(EventContext context) {
		return true;
	}
}