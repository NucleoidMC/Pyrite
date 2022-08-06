package fr.hugman.pyrite.game.phase;

import com.mojang.serialization.JsonOps;
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

public record PyriteWaiting(PyriteGame game, GameSpace gameSpace, TeamSelectionLobby teamSelection) {
	public static GameOpenProcedure open(GameOpenContext<PyriteConfig> context) {
		PyriteConfig config = context.config();
		MinecraftServer server = context.server();

		// Select a map
		PyriteMap map = PyriteMap.fromConfig(config, context.server().getRegistryManager());

		RuntimeWorldConfig worldConfig = new RuntimeWorldConfig().setGenerator(map.generator().getChunkGenerator(context.server()));

		return context.openWithWorld(worldConfig, (activity, world) -> {
			GameWaitingLobby.addTo(activity, map.playerConfig());

			TeamSelectionLobby teamSelection = TeamSelectionLobby.addTo(activity, map.teams());

			PyriteWaiting waiting = new PyriteWaiting(new PyriteGame(map, world), activity.getGameSpace(), teamSelection);

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
		// TODO: rules hologram depending on spawn point (with facing)
	}

	private GameResult requestStart() {
		var result = PyriteMap.CODEC.encodeStart(JsonOps.INSTANCE, this.game.map());
		this.gameSpace.getPlayers().sendMessage(Text.literal(result.get().left().get().toString()));
		return GameResult.error(Text.literal("This game has yet to be implemented. Come around later!"));
	}

	private ActionResult killPlayer(ServerPlayerEntity player, DamageSource source) {
		player.setHealth(20.0f);
		this.tpToSpawn(player);
		return ActionResult.FAIL;
	}

	private void tpToSpawn(ServerPlayerEntity player) {
		var spawn = getSpawn();
		var random = this.game.world().getRandom();
		BlockPos pos = new BlockPos(spawn.point().getRandom(this.game, random));
		ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4);
		this.game.world().getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
		player.teleport(this.game.world(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawn.angle().yaw().get(random), spawn.angle().pitch().get(random));
	}

	private Spawn getSpawn() {
		return this.game.map().getDefaultSpawn();
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		return offer.accept(this.game.world(), this.getSpawn().point().getRandom(this.game, this.game.world().getRandom())).and(() -> {
			ServerPlayerEntity player = offer.player();
			player.changeGameMode(GameMode.ADVENTURE);
			this.tpToSpawn(player);
		});
	}
}
