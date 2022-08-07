package fr.hugman.pyrite.context;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public enum EventPositionSelector implements StringIdentifiable {
	BLOCK("block", EventContext::blockPos),
	THIS("this_entity", context -> context.thisEntity().getPos()),
	TARGET("target_entity", context -> context.targetEntity().getPos());

	public static final com.mojang.serialization.Codec<EventPositionSelector> CODEC = StringIdentifiable.createCodec(EventPositionSelector::values);

	private final String id;
	private final Function<EventContext, Vec3d> function;

	EventPositionSelector(String id, Function<EventContext, Vec3d> function) {
		this.id = id;
		this.function = function;
	}

	@Override
	public String asString() {
		return null;
	}

	public Vec3d get(EventContext context) {
		return function.apply(context);
	}
}
