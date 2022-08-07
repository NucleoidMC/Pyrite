package fr.hugman.pyrite.map.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.trigger.PyriteTrigger;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.Optional;

public record PyriteListener(PyritePredicate predicate, PyriteTrigger trigger, Optional<Text> message) {
	public static final Codec<PyriteListener> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.fieldOf("predicate").orElse(PyritePredicate.DEFAULT).forGetter(PyriteListener::predicate),
			PyriteTrigger.CODEC.fieldOf("trigger").forGetter(PyriteListener::trigger),
			PlasmidCodecs.TEXT.optionalFieldOf("message").forGetter(PyriteListener::message)
	).apply(instance, PyriteListener::new));

	public ActionResult test(EventContext context) {
		if(!this.predicate.test(context)) return ActionResult.PASS;

		this.message.ifPresent(m -> context.thisEntity().sendMessage(m));
		return trigger.cancelsContext() ? ActionResult.FAIL : ActionResult.SUCCESS;
	}
}
