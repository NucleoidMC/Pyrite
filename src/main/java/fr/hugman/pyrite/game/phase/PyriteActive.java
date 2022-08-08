package fr.hugman.pyrite.game.phase;

import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.game.PlayerManager;
import fr.hugman.pyrite.game.PyriteGame;
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

public record PyriteActive(PyriteGame game) {
	public static GameResult transferActivity(PyritePreStart preStart) {
		preStart.game().space().setActivity(activity -> {
			var widgets = GlobalWidgets.addTo(activity);
			// TODO: sidebar
			preStart.game().setPlayerManager(PlayerManager.create(preStart.game(), TeamManager.addTo(activity), preStart.teamSelection()));

			var active = new PyriteActive(preStart.game());

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
			activity.listen(BlockBreakEvent.EVENT, active::breakBlock);
			activity.listen(BlockUseEvent.EVENT, active::useBlock);
		});
		return GameResult.ok();
	}

	private ActionResult killPlayer(ServerPlayerEntity player, DamageSource source) {
		//TODO: custom death (switch to FAIL!!!!! and make player respawn correctly)
		this.game.respawn(player);
		return ActionResult.FAIL;
	}

	private ActionResult dropItem(ServerPlayerEntity player, int i, ItemStack stack) {
		//TODO: expand region listener configs and context for players dropping items
		return ActionResult.SUCCESS;
	}

	private ActionResult placeBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state, ItemUsageContext context) {
		//TODO: this should instead look for the listeners that
		// return FAIL/CONSUME/CONSUME_PARTIAL/SUCCESS (in priority order)
		// FIRST and then triggers these ones (if there are any)
		for(var listenerConfig : this.game.map().listenerConfigs()) {
			var region = this.game.map().region(listenerConfig.regionKey());
			if(region != null && region.contains(this.game, pos)) {
				var place = listenerConfig.place();
				if(place.isPresent()) {
					var result = place.get().test(EventContext.create(this.game).entity(player).build());
					if(result != ActionResult.PASS) {
						return result;
					}
				}
			}
		}
		return ActionResult.SUCCESS;
	}

	private ActionResult breakBlock(ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
		//TODO: this should instead look for the listeners that
		// return FAIL/CONSUME/CONSUME_PARTIAL/SUCCESS (in priority order)
		// FIRST and then triggers these ones (if there are any)
		for(var listenerConfig : this.game.map().listenerConfigs()) {
			var region = this.game.map().region(listenerConfig.regionKey());
			if(region != null && region.contains(this.game, pos)) {
				var brek = listenerConfig.brek();
				if(brek.isPresent()) {
					var result = brek.get().test(EventContext.create(this.game).entity(player).build());
					if(result != ActionResult.PASS) {
						return result;
					}
				}
			}
		}
		return ActionResult.SUCCESS;
	}

	private ActionResult useBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
		//TODO: this should instead look for the listeners that
		// return FAIL/CONSUME/CONSUME_PARTIAL/SUCCESS (in priority order)
		// FIRST and then triggers these ones (if there are any)
		for(var listenerConfig : this.game.map().listenerConfigs()) {
			var region = this.game.map().region(listenerConfig.regionKey());
			if(region != null && region.contains(this.game, hitResult.getBlockPos())) {
				var use = listenerConfig.use();
				if(use.isPresent()) {
					var result = use.get().test(EventContext.create(this.game).entity(player).build());
					if(result != ActionResult.PASS) {
						return result;
					}
				}
			}
		}
		return ActionResult.SUCCESS;
	}

	private void enable() {
		//TODO: print map info (objectives mainly)
		//TODO: spawn players
		for(var player : this.game.onlinePlayers()) {
			this.game.respawn(player);
		}
	}

	private void tick() {
		// Check if players are entering or exiting regions
		for(var player : this.game.onlinePlayers()) {
			var playerData = this.game.playerManager().playerData(player);
			var lastPos = playerData.lastTickPos;
			var currentPos = player.getPos();
			if(!lastPos.equals(currentPos)) {
				for(var listenerConfig : this.game.map().listenerConfigs()) {
					var region = this.game.map().region(listenerConfig.regionKey());
					if(region != null) {
						if(region.enters(this.game, lastPos, currentPos)) {
							var enter = listenerConfig.enter();
							if(enter.isPresent()) {
								var result = enter.get().test(EventContext.create(this.game).entity(player).build());
								if(result != ActionResult.PASS) {
									if(result == ActionResult.FAIL) {
										player.teleport(this.game.world(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), player.getYaw(), player.getPitch());
									}
								}
							}
						}
						if(region.exits(this.game, lastPos, currentPos)) {
							var exit = listenerConfig.exit();
							if(exit.isPresent()) {
								var result = exit.get().test(EventContext.create(this.game).entity(player).build());
								if(result != ActionResult.PASS) {
									if(result == ActionResult.FAIL) {
										player.teleport(this.game.world(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), player.getYaw(), player.getPitch());
									}
								}
							}
						}
					}
				}
				playerData.lastTickPos = currentPos;
			}
		}
		//TODO: check win conditions
	}

	private PlayerOfferResult offerPlayer(PlayerOffer offer) {
		var player = offer.player();
		return offer.accept(this.game.world(), this.game.getSpawn(player).pos(this.game, player)).and(() -> {
			player.changeGameMode(GameMode.ADVENTURE);
			this.game.tpToSpawn(player);
		});
	}
}
