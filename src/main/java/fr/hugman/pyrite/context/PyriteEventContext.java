package fr.hugman.pyrite.context;

import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * Represents the context of an event that happens in the game.
 * It can refer to anything, like a player placing a block, or an entity being damaged.
 */
public record PyriteEventContext(
		PyriteGame game,
		Vec3d pos,
		BlockState state,
		Entity thisEntity,
		Entity targetEntity
) {
	static PyriteEventContext.Builder create(PyriteGame game) {
		return new PyriteEventContext.Builder(game);
	}

	static class Builder {
		private final PyriteGame game;
		private Vec3d pos;
		private BlockState state;
		private Entity entity;
		private Entity target;

		public Builder(PyriteGame game) {
			this.game = game;
		}

		public PyriteEventContext.Builder pos(Vec3d pos) {
			this.pos = pos;
			return this;
		}

		public PyriteEventContext.Builder state(BlockState state) {
			this.state = state;
			return this;
		}

		public PyriteEventContext.Builder entity(Entity thisEntity) {
			this.entity = thisEntity;
			return this;
		}

		public PyriteEventContext.Builder target(Entity targetEntity) {
			this.target = targetEntity;
			return this;
		}

		public PyriteEventContext build() {
			return new PyriteEventContext(this.game, this.pos, this.state, this.entity, this.target);
		}
	}
}
