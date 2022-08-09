package fr.hugman.pyrite.game;

import fr.hugman.pyrite.game.phase.PyriteActive;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.spawn.Spawn;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameSpacePlayers;

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
	@Nullable
	public PlayerManager playerManager() {return playerManager;}

	public void setPlayerManager(PlayerManager playerManager) {this.playerManager = playerManager;}

	public Random random() {return world.getRandom();}

	public GameSpacePlayers onlinePlayers() {return space.getPlayers();}

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
