package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.PyriteRegistries;

public interface PyriteTrigger {
	Codec<PyriteTrigger> CODEC = PyriteRegistries.TRIGGER_EVENT_TYPE.getCodec().dispatch(PyriteTrigger::getType, PyriteTriggerType::codec);

	PyriteTriggerType<?> getType();

	/**
	 * Should this trigger cancel the action that triggered it?
	 */
	default boolean cancelsContext() {return false;}
}
