package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;

public record NotPyritePredicate(PyritePredicate predicate) implements PyritePredicate {
	public static final Codec<NotPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.fieldOf("predicate").forGetter(NotPyritePredicate::predicate)
	).apply(instance, NotPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.NOT;
	}

	@Override
	public boolean test(EventContext context) {
		return !predicate.test(context);
	}
}