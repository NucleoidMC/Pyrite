package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.context.EventContext;

public record NeverPyritePredicate() implements PyritePredicate {
	public static final NeverPyritePredicate INSTANCE = new NeverPyritePredicate();

	public static final Codec<NeverPyritePredicate> CODEC = Codec.unit(INSTANCE);

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.NEVER;
	}

	@Override
	public boolean test(EventContext context) {
		return false;
	}
}