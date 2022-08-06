package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.PyriteEventContext;

public record ReferencePyritePredicate(String key) implements PyritePredicate {
	public static final Codec<ReferencePyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(ReferencePyritePredicate::key)
	).apply(instance, ReferencePyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.ALWAYS;
	}

	@Override
	public boolean test(PyriteEventContext context) {
		return context.game().predicate(key).test(context);
	}
}