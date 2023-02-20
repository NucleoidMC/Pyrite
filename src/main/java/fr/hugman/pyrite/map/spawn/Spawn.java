package fr.hugman.pyrite.map.spawn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.map.point.PointProvider;
import fr.hugman.pyrite.util.Angle;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

public record Spawn(PointProvider point, Angle angle, Optional<String> kit) {
	public static final Codec<Spawn> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PointProvider.CODEC.fieldOf("point").forGetter(Spawn::point),
			Angle.CODEC.fieldOf("angle").orElse(Angle.DEFAULT).forGetter(Spawn::angle),
			Codec.STRING.optionalFieldOf("kit").forGetter(Spawn::kit)
	).apply(instance, Spawn::new));

	public Vec3d pos(PyriteGame game, ServerPlayerEntity player) {
		return this.pos(game.random(), EventContext.create(game).entity(player).build());
	}

	public Vec3d pos(Random random, EventContext context) {
		return this.point.getRandom(random, context);
	}

	public float yaw(Random random) {
		return this.angle.yaw().get(random);
	}

	public float pitch(Random random) {
		return this.angle.pitch().get(random);
	}
}
