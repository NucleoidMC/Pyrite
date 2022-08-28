package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.PyriteRegistries;
import net.minecraft.util.registry.Registry;

public record PyriteTriggerType<P extends PyriteTrigger>(Codec<P> codec) {
	public static final PyriteTriggerType<CancelPyriteTrigger> CANCEL = register("cancel", CancelPyriteTrigger.CODEC);
	public static final PyriteTriggerType<ScorePyriteTrigger> SCORE = register("score", ScorePyriteTrigger.CODEC);

	private static <P extends PyriteTrigger> PyriteTriggerType<P> register(String name, Codec<P> codec) {
		return Registry.register(PyriteRegistries.TRIGGER_TYPE, Pyrite.id(name), new PyriteTriggerType<>(codec));
	}

	@Override
	public String toString() {
		var id = PyriteRegistries.TRIGGER_TYPE.getId(this);
		if(id == null) {
			return "UNKNOWN";
		}
		return id.toString();
	}
}
