package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;

import java.util.List;

public record AnyPyritePredicate(List<PyritePredicate> predicate) implements PyritePredicate {
	public static final Codec<AnyPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.listOf().fieldOf("predicates").forGetter(AnyPyritePredicate::predicate)
	).apply(instance, AnyPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.ANY;
	}

	@Override
	public boolean test(EventContext context) {
		return predicate.stream().anyMatch(p -> p.test(context));
	}
}