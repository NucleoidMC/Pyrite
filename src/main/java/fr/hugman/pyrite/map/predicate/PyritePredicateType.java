package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.registry.PyriteRegistries;
import net.minecraft.registry.Registry;

public record PyritePredicateType<P extends PyritePredicate>(Codec<P> codec) {
	public static final PyritePredicateType<ReferencePyritePredicate> REFERENCE = register("reference", ReferencePyritePredicate.CODEC);
	public static final PyritePredicateType<AlwaysPyritePredicate> ALWAYS = register("always", AlwaysPyritePredicate.CODEC);
	public static final PyritePredicateType<NeverPyritePredicate> NEVER = register("never", NeverPyritePredicate.CODEC);
	public static final PyritePredicateType<NotPyritePredicate> NOT = register("not", NotPyritePredicate.CODEC);
	public static final PyritePredicateType<AnyPyritePredicate> ANY = register("any", AnyPyritePredicate.CODEC);
	public static final PyritePredicateType<AllPyritePredicate> ALL = register("all", AllPyritePredicate.CODEC);
	public static final PyritePredicateType<TeamPyritePredicate> TEAM = register("team", TeamPyritePredicate.CODEC);
	public static final PyritePredicateType<RegionPyritePredicate> REGION = register("region", RegionPyritePredicate.CODEC);
	public static final PyritePredicateType<BlockPyritePredicate> BLOCK = register("block_predicate", BlockPyritePredicate.CODEC);

	private static <P extends PyritePredicate> PyritePredicateType<P> register(String name, Codec<P> codec) {
		return Registry.register(PyriteRegistries.PYRITE_PREDICATE_TYPE, Pyrite.id(name), new PyritePredicateType<>(codec));
	}

	@Override
	public String toString() {
		var id = PyriteRegistries.PYRITE_PREDICATE_TYPE.getId(this);
		if(id == null) {
			return "UNKNOWN";
		}
		return id.toString();
	}
}
