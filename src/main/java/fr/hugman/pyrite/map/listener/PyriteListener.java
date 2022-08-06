package fr.hugman.pyrite.map.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.PyriteEventContext;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.trigger.PyriteTrigger;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.Optional;
import java.util.function.Predicate;

public record PyriteListener(PyritePredicate predicate, PyriteTrigger trigger, Optional<Text> message) implements Predicate<PyriteEventContext> {
	public static final Codec<PyriteListener> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.fieldOf("predicate").forGetter(PyriteListener::predicate),
			PyriteTrigger.CODEC.fieldOf("trigger").forGetter(PyriteListener::trigger),
			PlasmidCodecs.TEXT.optionalFieldOf("message").forGetter(PyriteListener::message)
	).apply(instance, PyriteListener::new));

	@Override
	public boolean test(PyriteEventContext context) {
		if(!this.predicate.test(context)) return true;

		this.message.ifPresent(m -> context.thisEntity().sendMessage(m));
		return !trigger.cancelsContext();
	}
}
