package fr.hugman.pyrite.game;

import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.game.phase.PyriteActive;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.objective.progress.ProgressManager;
import fr.hugman.pyrite.map.spawn.Spawn;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameSpacePlayers;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

public final class PyriteGame {
	private final PyriteMap map;
	private final GameSpace space;
	private final ServerWorld world;
	private PlayerManager playerManager;
	private ProgressManager progressManager;
	private boolean ended;

	public PyriteGame(PyriteMap map, GameSpace space, ServerWorld world) {
		this.map = map;
		this.space = space;
		this.world = world;
		this.ended = false;
	}

	public PyriteMap map() {return map;}

	public GameSpace space() {return space;}

	public ServerWorld world() {return world;}

	/**
	 * Returns the player manager of the game. Can be null if the game has not reached the active state.
	 *
	 * @see PyriteActive#transferActivity
	 */
	public PlayerManager playerManager() {return playerManager;}

	public void setPlayerManager(PlayerManager playerManager) {this.playerManager = playerManager;}

	public ProgressManager progressManager() {
		return progressManager;
	}

	public void setProgressManager(ProgressManager progressManager) {
		this.progressManager = progressManager;
	}

	public boolean hasEnded() {
		return ended;
	}

	public Random random() {return world.getRandom();}

	public GameSpacePlayers onlinePlayers() {return space.getPlayers();}

	public PlayerSet onlinePlayers(GameTeamKey teamKey) {
		return this.playerManager.teamManager().playersIn(teamKey);
	}

	public void tick() {
		// Check if players are entering or exiting regions
		for(var player : this.onlinePlayers()) {
			var playerData = this.playerManager().playerData(player);
			var lastPos = playerData.lastTickPos;
			var currentPos = player.getPos();
			if(!lastPos.equals(currentPos)) {
				boolean cannotMove = false;
				for(var listenerConfig : this.map().playerListenerLists().move()) {
					var result = listenerConfig.test(EventContext.create(this).entity(player).build());
					if(result == ActionResult.FAIL) {
						cannotMove = true;
					}
				}
				if(cannotMove) player.teleport(this.world(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), player.getYaw(), player.getPitch());
				else playerData.lastTickPos = currentPos;
			}
		}
	}

	public void end() {
		this.ended = true;

		for(var player : this.onlinePlayers()) {
			player.changeGameMode(GameMode.SPECTATOR);
		}
	}

	public void updateWinningStatus(GameTeamKey teamKey) {
		var data = this.playerManager.teamData(teamKey);

		boolean wasEliminated = data.eliminated;
		boolean hasWon = data.won;
		boolean won = true;
		boolean eliminated = false;

		//TODO: special win/elim conditions
		if(this.progressManager.scoreProgress().isPresent()) {
			if(!this.progressManager.scoreProgress().get().finished(teamKey)) {
				won = false;
			}
			if(this.progressManager.scoreProgress().get().failed(teamKey)) {
				eliminated = true;
			}
		}

		if(won && !hasWon) {
			this.win(teamKey);
		}

		if(eliminated && !wasEliminated) {
			this.eliminate(teamKey);
		}
	}

	/**
	 * @return the last team that is not eliminated, or null if there is more than one team not eliminated.
	 */
	public GameTeamKey getLastTeam() {
		var teams = this.playerManager.teamKeys();
		// if there is only one team not eliminated, it wins
		var winningTeams = teams.stream().filter(team -> !this.playerManager.teamData(team).eliminated).toList();
		if(winningTeams.size() == 1) {
			return winningTeams.get(0);
		}
		return null;
	}

	public void win(GameTeamKey teamKey) {
		var data = this.playerManager.teamData(teamKey);
		data.won = true;
		this.sendMessage(Text.translatable("text.pyrite.team_won", this.playerManager.teamConfig(teamKey).name()));

		//TODO: special win amounts
		//TODO: leaderboards? (2nd team, 3rd team, etc.)
		this.end();
	}

	public void eliminate(GameTeamKey teamKey) {
		var data = this.playerManager.teamData(teamKey);
		data.eliminated = true;

		this.onlinePlayers(teamKey).forEach(player -> player.changeGameMode(GameMode.SPECTATOR));

		var lastTeam = this.getLastTeam();
		if(lastTeam != null) {
			// display win
			this.win(lastTeam);
		}
		else {
			// display elimination
			Text msg = Text.translatable("text.pyrite.team_eliminated", this.playerManager.teamConfig(teamKey).name());
			this.sendMessage(Text.literal("\n").append(msg).append("\n"));
			this.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST);
		}

	}

	public void playSound(SoundEvent sound) {this.onlinePlayers().playSound(sound);}

	public void sendMessage(Text message) {this.onlinePlayers().sendMessage(message);}

	public void respawn(ServerPlayerEntity player) {
		player.changeGameMode(GameMode.SURVIVAL);
		this.resetPlayer(player);
		this.tpToSpawn(player);
		//TODO: give kit
	}

	public void tpToSpawn(ServerPlayerEntity player) {
		this.tpToSpawn(player, this.getSpawn(player));
	}

	public void tpToSpawn(ServerPlayerEntity player, Spawn spawn) {
		var random = this.random();
		var pos = spawn.pos(this, player);
		this.world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, new ChunkPos((int) pos.getX() >> 4, (int) pos.getZ() >> 4), 1, player.getId());
		player.teleport(this.world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, spawn.yaw(random), spawn.pitch(random));
	}

	public void resetPlayer(ServerPlayerEntity player) {
		player.extinguish();
		player.fallDistance = 0.0F;
		player.getInventory().clear();
		player.getEnderChestInventory().clear();
		player.clearStatusEffects();
		player.getHungerManager().setFoodLevel(20);
		player.setExperienceLevel(0);
		player.setExperiencePoints(0);
		player.setHealth(player.getMaxHealth());
	}

	public Spawn getSpawn(ServerPlayerEntity player) {
		if(playerManager != null) {
			var teamKey = playerManager.teamKey(player);
			if(teamKey != null) {
				return this.map.spawn(teamKey.id());
			}
		}
		return this.map.getDefaultSpawn();
	}
}
