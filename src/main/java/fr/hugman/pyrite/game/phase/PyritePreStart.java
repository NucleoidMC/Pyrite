package fr.hugman.pyrite.game.phase;

import com.mojang.serialization.JsonOps;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.PyriteConfig;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.spawn.Spawn;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameOpenProcedure;
import xyz.nucleoid.plasmid.game.GameResult;
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

public record PyritePreStart(PyriteGame game, TeamSelectionLobby teamSelection) {

	public static GameOpenProcedure open(GameOpenContext<PyriteConfig> context) {
		PyriteConfig config = context.config();
		MinecraftServer server = context.server();

		var random = Random.createLocal();

		// Select a map
		PyriteMap map = config.randomMap(context.server().getRegistryManager(), random);

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig().setGenerator(map.generator().getChunkGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			GameWaitingLobby.addTo(activity, map.playerConfig());

			TeamSelectionLobby teamSelection = TeamSelectionLobby.addTo(activity, map.teams());

			PyritePreStart waiting = new PyritePreStart(new PyriteGame(map, activity.getGameSpace(), world), teamSelection);

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
		player.setHealth(20.0f);
		this.game.tpToSpawn(player);
		return ActionResult.FAIL;
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		var player = offer.player();
		return offer.accept(this.game.world(), this.game.map().getDefaultSpawn().pos(this.game, player)).and(() -> {
			player.changeGameMode(GameMode.ADVENTURE);
			this.game.tpToSpawn(player);
		});
	}
}
