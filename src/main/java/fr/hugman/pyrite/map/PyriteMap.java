package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.PyriteConfig;
import fr.hugman.pyrite.PyriteRegistries;
import fr.hugman.pyrite.map.kit.Kit;
import fr.hugman.pyrite.map.listener.RegionListeners;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.region.Region;
import fr.hugman.pyrite.map.spawn.RespawnSettings;
import fr.hugman.pyrite.map.spawn.Spawn;
import fr.hugman.pyrite.util.KeyableList;
import net.minecraft.util.registry.DynamicRegistryManager;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.GameTeamList;

import java.util.List;
import java.util.Objects;

public record PyriteMap(
		PyriteMapMetadata metadata,
		PyriteMapGenerator generator,
		PlayerConfig playerConfig,
		GameTeamList teams,
		KeyableList<String, Region> regions,
		KeyableList<String, Spawn> spawns,
		KeyableList<String, PyritePredicate> predicates,
		List<RegionListeners> listeners,
		KeyableList<String, Kit> kits,
		RespawnSettings respawnSettings
) {
	public static final Codec<PyriteMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteMapMetadata.MAP_CODEC.forGetter(PyriteMap::metadata),
			PyriteMapGenerator.CODEC.fieldOf("generator").forGetter(PyriteMap::generator),
			PlayerConfig.CODEC.fieldOf("player_config").forGetter(PyriteMap::playerConfig),
			GameTeamList.CODEC.fieldOf("teams").forGetter(PyriteMap::teams),
			KeyableList.codec(Region.CODEC).fieldOf("regions").forGetter(PyriteMap::regions),
			KeyableList.codec(Spawn.CODEC).fieldOf("spawns").forGetter(PyriteMap::spawns),
			KeyableList.codec(PyritePredicate.CODEC).fieldOf("predicates").forGetter(PyriteMap::predicates),
			RegionListeners.LIST_CODEC.fieldOf("listeners").forGetter(PyriteMap::listeners),
			KeyableList.codec(Kit.CODEC).fieldOf("kits").forGetter(PyriteMap::kits),
			RespawnSettings.CODEC.fieldOf("respawn").forGetter(PyriteMap::respawnSettings)
	).apply(instance, PyriteMap::new));

	public static PyriteMap fromConfig(PyriteConfig config, DynamicRegistryManager registryManager) {
		var maps = config.maps();
		var mapId = maps.get((int) Math.nextDown(maps.size()));
		return registryManager.get(PyriteRegistries.MAP.getKey()).get(mapId);
	}

	public GameTeam team(GameTeamKey key) {return this.teams.byKey(key);}

	public Region region(String key) {return this.regions.byKey(key);}

	public Spawn spawn(String key) {return this.spawns.byKey(key);}

	public PyritePredicate predicate(String key) {return this.predicates.byKey(key);}

	public Kit kit(String key) {return this.kits.byKey(key);}

	public Spawn getDefaultSpawn() {
		return Objects.requireNonNull(this.spawn("default"));
	}
}
