package fr.hugman.pyrite.context;

import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Represents the context of an event that happens in the game.
 * It can refer to anything, like a player placing a block, or an entitySelector being damaged.
 */
public record EventContext(
		PyriteGame game,
		Vec3d blockPos,
		BlockState state,
		Entity thisEntity,
		Entity targetEntity
) {

	public static EventContext.Builder create(PyriteGame game) {
		return new EventContext.Builder(game);
	}

	public static class Builder {
		private final PyriteGame game;
		private BlockState state;
		private Vec3d blockPos;
		private Entity entity;
		private Entity target;

		public Builder(PyriteGame game) {
			this.game = game;
		}

		public EventContext.Builder block(Vec3d blockPos, BlockState state) {
			this.blockPos = blockPos;
			this.state = state;
			return this;
		}

		public EventContext.Builder entity(Entity thisEntity) {
			this.entity = thisEntity;
			return this;
		}

		public EventContext.Builder target(Entity targetEntity) {
			this.target = targetEntity;
			return this;
		}

		public EventContext build() {
			return new EventContext(this.game, this.blockPos, this.state, this.entity, this.target);
		}
	}
}
