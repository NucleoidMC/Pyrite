package fr.hugman.pyrite.game.phase;

import fr.hugman.pyrite.PyriteConfig;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public record PyritePreStart(PyriteMap map, GameSpace space, ServerWorld world, TeamSelectionLobby teamSelection) {
	public static GameOpenProcedure open(GameOpenContext<PyriteConfig> context) {
		var config = context.config();

		var random = Random.createLocal();

		// TODO: map selection
		PyriteMap map = config.randomMap(random);

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig()
				.setGenerator(map.generator().getChunkGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			GameWaitingLobby.addTo(activity, map.playerConfig());

			TeamSelectionLobby teamSelection = TeamSelectionLobby.addTo(activity, map.teams());

			PyritePreStart waiting = new PyritePreStart(map, activity.getGameSpace(), world, teamSelection);

			activity.setRule(GameRuleType.INTERACTION, ActionResult.FAIL);

			activity.listen(GameActivityEvents.ENABLE, waiting::enable);
			activity.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
			activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);

			activity.listen(PlayerDamageEvent.EVENT, (player, source, amount) -> ActionResult.FAIL);
			activity.listen(PlayerDeathEvent.EVENT, waiting::killPlayer);
			activity.listen(PlayerAttackEntityEvent.EVENT, (attacker, hand, attacked, hitResult) -> ActionResult.FAIL);
		});
	}

	private void enable() {
		//TODO: rules hologram depending on spawn point (with facing)
	}

	private GameResult requestStart() {
		return PyriteActive.transferActivity(this);
	}

	private ActionResult killPlayer(ServerPlayerEntity player, DamageSource source) {
		this.resetPlayer(player);
		return ActionResult.FAIL;
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		var player = offer.player();

		var spawn = this.map.getDefaultSpawn();
		var pos = this.map.getDefaultSpawn().pos(this.world.random, null);
		return offer.accept(this.world, pos).and(() -> {
			player.setHealth(20.0f);
			player.changeGameMode(GameMode.ADVENTURE);
			this.world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos((int) pos.getX() >> 4, (int) pos.getZ() >> 4), 1, player.getId());
			player.teleport(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawn.yaw(this.world.random), spawn.pitch(this.world.random));
		});
	}

	public void resetPlayer(ServerPlayerEntity player) {
		var spawn = this.map.getDefaultSpawn();
		var pos = this.map.getDefaultSpawn().pos(this.world.random, null);

		player.setHealth(20.0f);
		player.changeGameMode(GameMode.ADVENTURE);
		this.world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos((int) pos.getX() >> 4, (int) pos.getZ() >> 4), 1, player.getId());
		player.teleport(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawn.yaw(this.world.random), spawn.pitch(this.world.random));
	}
}
