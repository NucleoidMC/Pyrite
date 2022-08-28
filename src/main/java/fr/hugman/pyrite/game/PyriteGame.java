package fr.hugman.pyrite.game;

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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameSpacePlayers;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

import java.util.List;

public final class PyriteGame {
	private final PyriteMap map;
	private final GameSpace space;
	private final ServerWorld world;
	private PlayerManager playerManager;

	public PyriteGame(PyriteMap map, GameSpace space, ServerWorld world) {
		this.map = map;
		this.space = space;
		this.world = world;
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

	public Random random() {return world.getRandom();}

	public GameSpacePlayers onlinePlayers() {return space.getPlayers();}

	public PlayerSet onlinePlayers(GameTeamKey teamKey) {
		return this.playerManager.teamManager().playersIn(teamKey);
	}

	public void updateWinningStatus(GameTeamKey teamKey) {
		var data = this.playerManager.teamData(teamKey);
		boolean wasEliminated = data.eliminated;
		if(this.playerManager.progressManager().scoreProgress().isPresent()) {
			if(this.playerManager.progressManager().scoreProgress().get().isWinning(teamKey)) {
				data.isWinning = true;
			}
			if(this.playerManager.progressManager().scoreProgress().get().shouldBeEliminated(teamKey)) {
				data.eliminated = true;
			}
		}

		if(data.eliminated && !wasEliminated) {
			this.eliminate(teamKey);
		}
	}

	public void eliminate(GameTeamKey teamKey) {
		// TODO: put team members in spectator
		this.onlinePlayers(teamKey).forEach(player -> player.changeGameMode(GameMode.SPECTATOR));

		Text msg = Text.translatable("text.the_towers.team_eliminated", this.playerManager.teamConfig(teamKey).name());

		this.sendMessage(Text.literal("\n").append(msg).append("\n"));
		this.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST);
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
