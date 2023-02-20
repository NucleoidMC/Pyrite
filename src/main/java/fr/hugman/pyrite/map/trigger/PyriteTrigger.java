package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.registry.PyriteRegistries;
import fr.hugman.pyrite.context.EventContext;
import net.minecraft.util.ActionResult;

public interface PyriteTrigger {
	Codec<PyriteTrigger> CODEC = PyriteRegistries.TRIGGER_TYPE.getCodec().dispatch(PyriteTrigger::getType, PyriteTriggerType::codec);

	PyriteTriggerType<?> getType();

	/**
	 * Should this trigger cancel the action that triggered it?
	 */
	ActionResult trigger(EventContext context);
}
