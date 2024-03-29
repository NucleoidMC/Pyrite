package fr.hugman.pyrite.game.phase;

import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.game.PlayerManager;
import fr.hugman.pyrite.game.PyriteGame;
import fr.hugman.pyrite.game.PyriteSidebar;
import fr.hugman.pyrite.map.objective.progress.ProgressManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameCloseReason;
import xyz.nucleoid.plasmid.game.GameResult;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.event.GameActivityEvents;
import xyz.nucleoid.plasmid.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.stimuli.event.block.BlockBreakEvent;
import xyz.nucleoid.stimuli.event.block.BlockPlaceEvent;
import xyz.nucleoid.stimuli.event.block.BlockUseEvent;
import xyz.nucleoid.stimuli.event.item.ItemThrowEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public final class PyriteActive {
	private final PyriteGame game;
	private final PyriteSidebar sidebar;
	public long tick = 0;
	public long closeTick = -1L;

	public PyriteActive(PyriteGame game, PyriteSidebar sidebar) {
		this.game = game;
		this.sidebar = sidebar;
	}

	public static GameResult transferActivity(PyritePreStart preStart) {
		preStart.space().setActivity(activity -> {
			var game = new PyriteGame(preStart.map(), activity.getGameSpace(), preStart.world());
			var playerManager = PlayerManager.create(game, TeamManager.addTo(activity), preStart.teamSelection());
			var progressManager = new ProgressManager(game);
			game.setPlayerManager(playerManager);
			game.setProgressManager(progressManager);

			var widgets = GlobalWidgets.addTo(activity);
			var sidebar = PyriteSidebar.create(game, widgets);

			var active = new PyriteActive(game, sidebar);

			activity.allow(GameRuleType.CRAFTING);
			activity.deny(GameRuleType.PORTALS);
			activity.allow(GameRuleType.PVP);
			activity.allow(GameRuleType.HUNGER);
			activity.allow(GameRuleType.FALL_DAMAGE);
			activity.allow(GameRuleType.INTERACTION);
			activity.allow(GameRuleType.BLOCK_DROPS);
			activity.allow(GameRuleType.THROW_ITEMS);

			activity.listen(GameActivityEvents.ENABLE, active::enable);
			activity.listen(GameActivityEvents.TICK, active::tick);

			activity.listen(GamePlayerEvents.OFFER, active::offerPlayer);

			activity.listen(PlayerDeathEvent.EVENT, active::killPlayer);
			activity.listen(ItemThrowEvent.EVENT, active::dropItem);
			activity.listen(BlockPlaceEvent.BEFORE, active::placeBlock);
			activity.listen(BlockPlaceEvent.AFTER, active::placedBlock);
			activity.listen(BlockBreakEvent.EVENT, active::breakBlock);
			activity.listen(BlockUseEvent.EVENT, active::useBlock);
		});
		return GameResult.ok();
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		if(this.game.hasEnded()) {

		}
		//TODO: team re-joining
		var player = offer.player();
		return offer.accept(this.game.world(), this.game.getSpawn(player).pos(this.game.random(), EventContext.create(game).entity(player).build())).and(() -> {
			player.changeGameMode(GameMode.ADVENTURE);
			this.game.tpToSpawn(player);
		});
	}

	private ActionResult killPlayer(ServerPlayerEntity player, DamageSource source) {
		//TODO: custom death (switch to FAIL!!!!! and make player respawn correctly)
		this.game.respawn(player);
		return ActionResult.FAIL;
	}

	private ActionResult dropItem(ServerPlayerEntity player, int i, ItemStack stack) {
		//TODO: expand region listener configs and context for players dropping items
		return ActionResult.PASS;
	}

	private ActionResult placeBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context) {
		for(var listenerConfig : this.game.map().playerListenerLists().placeBlock()) {
			var result = listenerConfig.test(EventContext.create(this.game).entity(player).blockPos(pos).build());
			if(result == ActionResult.PASS) continue;
			return result;
		}
		return ActionResult.PASS;
	}

	private void placedBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState blockState) {
		for(var listenerConfig : this.game.map().playerListenerLists().placeBlock()) {
			var result = listenerConfig.test(EventContext.create(this.game).entity(player).blockPos(pos).build());
			if(result == ActionResult.FAIL) world.breakBlock(pos, true);
		}
	}

	private ActionResult breakBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
		for(var listenerConfig : this.game.map().playerListenerLists().breakBlock()) {
			var result = listenerConfig.test(EventContext.create(this.game).entity(player).blockPos(pos).build());
			if(result == ActionResult.PASS) continue;
			return result;
		}
		return ActionResult.PASS;
	}

	private ActionResult useBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		for(var listenerConfig : this.game.map().playerListenerLists().useBlock()) {
			var result = listenerConfig.test(EventContext.create(this.game).entity(player).blockPos(hitResult.getBlockPos()).build());
			if(result == ActionResult.PASS) continue;
			return result;
		}
		return ActionResult.PASS;
	}

	private void enable() {
		//TODO: print map info (objectives mainly)
		for(var player : this.game.onlinePlayers()) {
			this.game.respawn(player);
		}
		this.game.progressManager().scoreProgress().ifPresent(scoreObjective -> this.game.playerManager().teamKeys().forEach(scoreObjective::points));
	}

	private void tick() {
		this.tick++;

		// Game is active
		if(!this.game.hasEnded()) {
			this.game.tick();
			if(this.tick % 20 == 0) this.sidebar.update(this.tick);
		}
		// Game is inactive
		else {
			// Game is ending on this specific tick
			if(this.closeTick == -1L) {
				// TODO: scale end duration based on tick
				long endDuration = 15 * 20L;
				this.closeTick = this.tick + endDuration;
			}
			// The space is closing
			else if(this.tick == this.closeTick) {
				this.game.space().close(GameCloseReason.FINISHED);
			}
		}
	}
}
