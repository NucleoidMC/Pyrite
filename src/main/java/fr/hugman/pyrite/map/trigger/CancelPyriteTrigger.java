package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;

public class CancelPyriteTrigger implements PyriteTrigger {
	public static final CancelPyriteTrigger INSTANCE = new CancelPyriteTrigger();

	public static final Codec<CancelPyriteTrigger> CODEC = Codec.unit(INSTANCE);

	@Override
	public PyriteTriggerType<?> getType() {
		return PyriteTriggerType.CANCEL;
	}

	@Override
	public boolean cancelsContext() {
		return true;
	}
}
