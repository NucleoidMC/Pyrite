package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.PyriteEventContext;

import java.util.List;

public record AllPyritePredicate(List<PyritePredicate> predicate) implements PyritePredicate {
	public static final Codec<AllPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.listOf().fieldOf("predicates").forGetter(AllPyritePredicate::predicate)
	).apply(instance, AllPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.ALL;
	}

	@Override
	public boolean test(PyriteEventContext context) {
		for(var predicate : predicate) {
			if(!predicate.test(context)) {
				return false;
			}
		}
		return true;
	}
}