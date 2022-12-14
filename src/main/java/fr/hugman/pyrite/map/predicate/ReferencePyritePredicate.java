package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;

public record ReferencePyritePredicate(String predicateKey) implements PyritePredicate {
	public static final Codec<ReferencePyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("predicate").forGetter(ReferencePyritePredicate::predicateKey)
	).apply(instance, ReferencePyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.REFERENCE;
	}

	@Override
	public boolean test(EventContext context) {
		return context.game().map().predicate(predicateKey).test(context);
	}
}