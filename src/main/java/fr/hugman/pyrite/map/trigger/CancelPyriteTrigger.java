package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.Optional;

public record CancelPyriteTrigger(Optional<Text> message) implements PyriteTrigger {
	public static final Codec<CancelPyriteTrigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlasmidCodecs.TEXT.optionalFieldOf("message").forGetter(CancelPyriteTrigger::message)
	).apply(instance, CancelPyriteTrigger::new));

	@Override
	public PyriteTriggerType<?> getType() {
		return PyriteTriggerType.CANCEL;
	}

	@Override
	public ActionResult trigger(EventContext context) {
		this.message.ifPresent(m -> {
			if(context.thisEntity() != null) context.thisEntity().sendMessage(m);
		});
		return ActionResult.FAIL;
	}
}
