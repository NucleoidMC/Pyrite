package fr.hugman.pyrite.game;

import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.plasmid.game.GameActivity;

public final class PyriteGame {
	private final PyriteMap map;
	private final ServerWorld world;
	private PlayerManager playerManager;

	public PyriteGame(PyriteMap map, ServerWorld world) {
		this.map = map;
		this.world = world;
	}

	public void createPlayerManager(GameActivity activity) {
		this.playerManager = PlayerManager.create(activity);
	}

	public PyriteMap map() {return map;}
	public ServerWorld world() {return world;}
	public PlayerManager playerManager() {return playerManager;}
}
