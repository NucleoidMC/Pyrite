package fr.hugman.pyrite.game;

import fr.hugman.pyrite.game.phase.PyriteActive;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.spawn.Spawn;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.GameSpacePlayers;
import xyz.nucleoid.plasmid.util.PlayerRef;

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
		//TODO: give kit
		this.tpToSpawn(player);
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

	public Spawn getSpawn(ServerPlayerEntity player) {
		if(playerManager != null) {
			var teamKey = playerManager.teamKey(player);
			if(teamKey != null) {
				var spawn = this.map.spawn(teamKey.id());
				if(spawn == null) throw new GameOpenException(Text.literal("Missing spawn for team " + teamKey.id()));
				return spawn;
			}
		}
		return this.map.getDefaultSpawn();
	}
}
