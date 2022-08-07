package fr.hugman.pyrite.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameActivity;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.GameTeamList;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;
import xyz.nucleoid.plasmid.game.common.team.TeamSelectionLobby;
import xyz.nucleoid.plasmid.game.player.PlayerSet;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record PlayerManager(TeamManager teamManager, Map<UUID, PlayerData> playerDataMap, Map<GameTeamKey, TeamData> teamDataMap) {
	public static PlayerManager create(PyriteGame game, TeamManager teamManager, TeamSelectionLobby selectionLobby) {
		var manager = new PlayerManager(teamManager, new Object2ObjectOpenHashMap<>(), new Object2ObjectOpenHashMap<>());

		for(GameTeam team : game.map().teams()) {
			team = team.withConfig(GameTeamConfig.builder(team.config())
					.setFriendlyFire(false)
					.setCollision(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS)
					.build());
			manager.teamManager.addTeam(team);
			manager.teamDataMap.put(team.key(), new TeamData());
		}

		selectionLobby.allocate(game.space().getPlayers(), (teamKey, player) -> manager.addPlayerToTeam(player, teamKey));
		return manager;
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

	public PlayerSet onlinePlayers(GameTeamKey teamKey) {
		return this.teamManager.playersIn(teamKey);
	}

	public boolean isSpectator(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player) == null;
	}

	public void addPlayerToTeam(ServerPlayerEntity player, GameTeamKey teamKey) {
		this.playerDataMap.computeIfAbsent(player.getUuid(), uuid -> PlayerData.create(player));
		this.teamManager.addPlayerTo(player, teamKey);
	}
}
