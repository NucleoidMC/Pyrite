package fr.hugman.pyrite.game;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.TeamManager;

import java.util.Map;
import java.util.UUID;

public record PlayerManager(TeamManager teamManager, Map<UUID, PlayerData> playerDataMap, Map<GameTeamKey, TeamData> teamDataMap) {
	public boolean isSpectator(ServerPlayerEntity player) {
		return this.teamManager.teamFor(player) == null;
	}
}
