package fr.hugman.pyrite.context;

import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Function;

public enum EventEntitySelector implements StringIdentifiable {
	THIS("this_entity", EventContext::thisEntity),
	TARGET("target_entity", EventContext::targetEntity);

	public static final com.mojang.serialization.Codec<EventEntitySelector> CODEC = StringIdentifiable.createCodec(EventEntitySelector::values);

	private final String id;
	private final Function<EventContext, Entity> function;

	EventEntitySelector(String id, Function<EventContext, Entity> function) {
		this.id = id;
		this.function = function;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public Entity get(EventContext context) {
		return function.apply(context);
	}
}
