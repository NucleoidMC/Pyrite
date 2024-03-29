package fr.hugman.pyrite.map.predicate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import fr.hugman.pyrite.registry.PyriteRegistries;
import fr.hugman.pyrite.context.EventContext;

import java.util.function.Function;
import java.util.function.Predicate;

public interface PyritePredicate extends Predicate<EventContext> {
	Codec<Either<String, PyritePredicate>> PREDICATE_CODEC = Codec.either(Codec.STRING, PyriteRegistries.PYRITE_PREDICATE_TYPE.getCodec().dispatch(PyritePredicate::getType, PyritePredicateType::codec));

	Codec<PyritePredicate> CODEC = PREDICATE_CODEC.xmap(
			either -> either.map(ReferencePyritePredicate::new, Function.identity()),
			provider -> provider instanceof ReferencePyritePredicate reference ? Either.left(reference.predicateKey()) : Either.right(provider)
	);

	PyritePredicate DEFAULT = AlwaysPyritePredicate.INSTANCE;

	PyritePredicateType<?> getType();
}
