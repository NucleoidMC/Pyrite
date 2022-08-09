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

public record Listener(PyritePredicate predicate, PyriteTrigger trigger, Optional<Text> message) {
	public static final Codec<Listener> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyritePredicate.CODEC.fieldOf("predicate").orElse(PyritePredicate.DEFAULT).forGetter(Listener::predicate),
			PyriteTrigger.CODEC.fieldOf("trigger").forGetter(Listener::trigger),
			PlasmidCodecs.TEXT.optionalFieldOf("message").forGetter(Listener::message)
	).apply(instance, Listener::new));

	public ActionResult test(EventContext context) {
		if(!this.predicate.test(context)) return ActionResult.PASS;

		this.message.ifPresent(m -> {
			if(context.thisEntity() != null) context.thisEntity().sendMessage(m);
		});
		return trigger.cancelsContext() ? ActionResult.FAIL : ActionResult.SUCCESS;
	}
}
