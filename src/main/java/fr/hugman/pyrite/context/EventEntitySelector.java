package fr.hugman.pyrite.context;

import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public enum EventEntitySelector implements StringIdentifiable {
	THIS("this_entity", EventContext::thisEntity, context -> {
		if(context.thisEntity() == null) return null;
		return context.thisEntity().getPos();
	}),
	BLOCK("block", THIS.entityFunction, context -> {
		if(context.blockPos() == null) return THIS.posFunction.apply(context);
		return Vec3d.of((context.blockPos()));
	}),
	TARGET("target_entity", EventContext::targetEntity, context -> {
		if(context.targetEntity() == null) return null;
		return context.targetEntity().getPos();
	});

	public static final com.mojang.serialization.Codec<EventEntitySelector> CODEC = StringIdentifiable.createCodec(EventEntitySelector::values);
	private final String id;
	private final Function<EventContext, Entity> entityFunction;
	private final Function<EventContext, Vec3d> posFunction;

	EventEntitySelector(String id, Function<EventContext, Entity> entityFunction, Function<EventContext, Vec3d> posFunction) {
		this.id = id;
		this.entityFunction = entityFunction;
		this.posFunction = posFunction;
	}

	@Override
	public String asString() {
		return this.id;
	}

	public Entity entity(EventContext context) {
		return this.entityFunction.apply(context);
	}

	public Vec3d pos(EventContext context) {
		return this.posFunction.apply(context);
	}
}
