package fr.hugman.pyrite.game;

import fr.hugman.pyrite.map.objective.progress.ProgressManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record PlayerManager(TeamManager teamManager, ProgressManager progressManager, Map<UUID, PlayerData> playerDataMap, Map<GameTeamKey, TeamData> teamDataMap) {
	public static PlayerManager create(PyriteGame game, TeamManager teamManager, TeamSelectionLobby selectionLobby) {
		var playerManager = new PlayerManager(teamManager, new ProgressManager(game), new Object2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>());

		for(GameTeam team : game.map().teams()) {
			team = team.withConfig(GameTeamConfig.builder(team.config())
					.setFriendlyFire(false)
					.setCollision(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)
					.build());
			playerManager.teamManager.addTeam(team);
			playerManager.teamDataMap.put(team.key(), TeamData.create());
		}

		selectionLobby.allocate(game.space().getPlayers(), (teamKey, player) -> playerManager.addPlayerToTeam(player, teamKey));
		return playerManager;
	}

	public Set<GameTeamKey> teamKeys() {
		return this.teamDataMap.keySet();
	}

	@Nullable
	public GameTeamKey teamKey(PlayerRef player) {
		return this.teamManager.teamFor(player);
	}

	@Nullable
	public GameTeamKey teamKey(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player);
	}

	public GameTeamConfig teamConfig(GameTeamKey key) {
		var config = this.teamManager.getTeamConfig(key);
		if(config == null) throw new GameOpenException(Text.literal("Could not find team with key '" + key + "'"));
		return config;
	}

	public TeamData teamData(GameTeamKey teamKey) {
		var data = this.teamDataMap.get(teamKey);
		if(data == null) throw new GameOpenException(Text.literal("Could not find team with key '" + teamKey + "'"));
		return data;
	}

	public Set<UUID> uuids() {
		return this.playerDataMap.keySet();
	}

	public PlayerData playerData(ServerPlayerEntity player) {
		return this.playerDataMap.get(player.getUuid());
	}

	public boolean isSpectator(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player) == null;
	}

	public void addPlayerToTeam(ServerPlayerEntity player, GameTeamKey teamKey) {
		this.playerDataMap.computeIfAbsent(player.getUuid(), uuid -> PlayerData.create(player));
		this.teamManager.addPlayerTo(player, teamKey);
	}
}
