package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;

public record BlockPyritePredicate(BlockPredicate predicate) implements PyritePredicate {
	public static final Codec<BlockPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockPredicate.BASE_CODEC.fieldOf("predicate").forGetter(BlockPyritePredicate::predicate)
	).apply(instance, BlockPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.BLOCK;
	}

	@Override
	public boolean test(EventContext context) {
		return predicate.test(context.game().world(), context.blockPos());
	}
}