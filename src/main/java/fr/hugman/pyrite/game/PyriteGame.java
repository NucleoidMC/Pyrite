package fr.hugman.pyrite.game;

import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.kit.Kit;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.region.Region;
import fr.hugman.pyrite.map.spawn.Spawn;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.Objects;

public record PyriteGame(PyriteMap map, PlayerManager playerManager) {
	public GameTeam team(GameTeamKey key) {
		return this.map.teams().byKey(key);
	}

	@Nullable
	public GameTeamKey teamKey(PlayerRef player) {
		return this.playerManager.teamManager().teamFor(player);
	}

	@Nullable
	public GameTeamKey teamKey(ServerPlayerEntity player) {
		return this.playerManager.teamManager().teamFor(player);
	}

	public boolean isSpectator(ServerPlayerEntity player) {
		return this.playerManager.isSpectator(player);
	}

	public Region region(String key) {
		return Objects.requireNonNull(this.map.regions().byKey(key));
	}

	public Spawn spawn(String key) {
		return Objects.requireNonNull(this.map.spawns().byKey(key));
	}

	public PyritePredicate predicate(String key) {
		return Objects.requireNonNull(this.map.predicates().byKey(key));
	}

	public Kit kit(String key) {
		return Objects.requireNonNull(this.map.kits().byKey(key));
	}
}
