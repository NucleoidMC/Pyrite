package fr.hugman.pyrite.game;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameActivity;
import xyz.nucleoid.plasmid.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.player.PlayerSet;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record PlayerManager(TeamManager teamManager, Map<UUID, PlayerData> playerDataMap, Map<GameTeamKey, TeamData> teamDataMap) {
	public static PlayerManager create(GameActivity activity) {
		TeamManager teamManager = TeamManager.addTo(activity);
		return new PlayerManager(teamManager, Map.of(), Map.of());
	}

	public Set<GameTeamKey> teamKeys() {
		return this.teamDataMap.keySet();
	}

	@Nullable
	public GameTeamKey teamKey(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player);
	}

	public GameTeamConfig teamConfig(GameTeamKey teamKey) {
		return Objects.requireNonNull(this.teamManager.getTeamConfig(teamKey));
	}

	public TeamData teamData(GameTeamKey teamKey) {
		return Objects.requireNonNull(this.teamDataMap.get(teamKey));
	}

	public Set<UUID> uuids() {
		return this.playerDataMap.keySet();
	}

	public PlayerData playerData(ServerPlayerEntity player) {
		return this.playerDataMap.get(player.getUuid());
	}

	public PlayerSet onlinePlayersIn(GameTeamKey teamKey) {
		return this.teamManager.playersIn(teamKey);
	}

	public boolean isSpectator(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player) == null;
	}

	public void addPlayerToTeam(ServerPlayerEntity player, GameTeamKey teamKey) {
		this.playerDataMap.put(player.getUuid(), PlayerData.create());
		this.teamManager.addPlayerTo(player, teamKey);
	}
}
