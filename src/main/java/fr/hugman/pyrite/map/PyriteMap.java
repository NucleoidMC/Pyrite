package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.PyriteConfig;
import fr.hugman.pyrite.map.kit.Kit;
import fr.hugman.pyrite.map.listener.RegionListenerConfigs;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.region.Region;
import fr.hugman.pyrite.map.spawn.RespawnSettings;
import fr.hugman.pyrite.map.spawn.Spawn;
import fr.hugman.pyrite.util.KeyableList;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.GameTeamList;

import java.util.List;
import java.util.Objects;

/**
 * Entire configuration for a map.
 * <p>
 * Map configuration will get created by reading the content of the {@code map} folder of all loaded data packs. The Codec data structure is defined in {@link PyriteMap#CODEC}.
 *
 * @param metadata        the metadata
 * @param generator       the map generator
 * @param playerConfig    the player configuration
 * @param teams           the list of teams
 * @param regions         the keyed list of all regions (empty by default)
 * @param spawns          the keyed list of all spawns (requires a region with ID 'default')
 * @param predicates      the keyed list of all predicates (empty by default)
 * @param listenerConfigs       the list of all listenerConfigs (empty by default)
 * @param kits            the keyed list of all kits (empty by default)
 * @param respawnSettings the respawn settings - optional
 *
 * @author Hugman
 * @see PyriteConfig#maps() location of the map within the Pyrite configuration
 * @since 1.0.0
 */
public record PyriteMap(
		PyriteMapMetadata metadata,
		PyriteMapGenerator generator,
		PlayerConfig playerConfig,
		GameTeamList teams,
		KeyableList<String, Region> regions,
		KeyableList<String, Spawn> spawns,
		KeyableList<String, PyritePredicate> predicates,
		List<RegionListenerConfigs> listenerConfigs,
		KeyableList<String, Kit> kits,
		RespawnSettings respawnSettings
) {
	public static final Codec<PyriteMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteMapMetadata.MAP_CODEC.forGetter(PyriteMap::metadata),
			PyriteMapGenerator.CODEC.fieldOf("generator").forGetter(PyriteMap::generator),
			PlayerConfig.CODEC.fieldOf("player_config").forGetter(PyriteMap::playerConfig),
			GameTeamList.CODEC.fieldOf("teams").forGetter(PyriteMap::teams),
			KeyableList.codec(Region.CODEC).fieldOf("regions").orElse(KeyableList.empty()).forGetter(PyriteMap::regions),
			KeyableList.codec(Spawn.CODEC).fieldOf("spawns").orElse(KeyableList.empty()).forGetter(PyriteMap::spawns),
			KeyableList.codec(PyritePredicate.CODEC).fieldOf("predicates").orElse(KeyableList.empty()).forGetter(PyriteMap::predicates),
			RegionListenerConfigs.LIST_CODEC.fieldOf("listener_configs").orElse(List.of()).forGetter(PyriteMap::listenerConfigs),
			KeyableList.codec(Kit.CODEC).fieldOf("kits").orElse(KeyableList.empty()).forGetter(PyriteMap::kits),
			RespawnSettings.CODEC.fieldOf("respawn").orElse(RespawnSettings.DEFAULT).forGetter(PyriteMap::respawnSettings)
	).apply(instance, PyriteMap::new));

	public GameTeam team(GameTeamKey key) {return this.teams.byKey(key);}

	public Region region(String key) {return this.regions.byKey(key);}

	public Spawn spawn(String key) {return this.spawns.byKey(key);}

	public PyritePredicate predicate(String key) {return this.predicates.byKey(key);}

	public Kit kit(String key) {return this.kits.byKey(key);}

	public Spawn getDefaultSpawn() {
		return Objects.requireNonNull(this.spawn("default"));
	}
}
