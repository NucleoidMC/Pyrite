package fr.hugman.pyrite.context;

import fr.hugman.pyrite.game.PyriteGame;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the context of an event that happens in the game.
 * It can refer to anything, like a player placing a block, or an entitySelector being damaged.
 *
 * @param game         the current game
 * @param blockPos     the block position (when placed, broken, used...)
 * @param thisEntity   the original entity triggering the event
 * @param targetEntity the entity affected by the event
 *
 * @author Hugman
 * @since 1.0.0
 */
public record EventContext(
		PyriteGame game,
		@Nullable BlockPos blockPos,
		@Nullable Entity thisEntity,
		@Nullable Entity targetEntity
) {
	public static EventContext.Builder create(PyriteGame game) {
		return new EventContext.Builder(game);
	}

	public static class Builder {
		private final PyriteGame game;
		private BlockPos blockPos;
		private Entity entity;
		private Entity target;

		public Builder(PyriteGame game) {
			this.game = game;
		}

		public EventContext.Builder blockPos(BlockPos blockPos) {
			this.blockPos = blockPos;
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
			return new EventContext(this.game, this.blockPos, this.entity, this.target);
		}
	}
}
