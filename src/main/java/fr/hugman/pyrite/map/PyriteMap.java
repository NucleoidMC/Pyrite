package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.PyriteConfig;
import fr.hugman.pyrite.map.kit.Kit;
import fr.hugman.pyrite.map.listener.PlayerListenerLists;
import fr.hugman.pyrite.map.objective.ObjectivesConfig;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.region.Region;
import fr.hugman.pyrite.map.spawn.RespawnSettings;
import fr.hugman.pyrite.map.spawn.Spawn;
import fr.hugman.pyrite.util.KeyableList;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;
import xyz.nucleoid.plasmid.game.common.team.GameTeam;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;
import xyz.nucleoid.plasmid.game.common.team.GameTeamList;

/**
 * Entire configuration for a map.
 * <p>
 * Map configuration will get created by reading the content of the {@code map} folder of all loaded data packs. The Codec data structure is defined in {@link PyriteMap#CODEC}.
 *
 * @param metadata            the metadata
 * @param generator           the map generator
 * @param playerConfig        the player configuration
 * @param teams               the list of teams
 * @param regions             the keyed list of all regions (empty by default)
 * @param respawnSettings     the respawn settings - optional
 * @param spawns              the keyed list of all spawns (requires a region with ID 'default')
 * @param predicates          the keyed list of all predicates (empty by default)
 * @param playerListenerLists the list of all listenerConfigs (empty by default)
 * @param kits                the keyed list of all kits (empty by default)
 * @param objectives          the configuration for the objectives (requires at least one of the sub-configurations)
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
		RespawnSettings respawnSettings,
		KeyableList<String, PyritePredicate> predicates,
		PlayerListenerLists playerListenerLists,
		KeyableList<String, Kit> kits,
		ObjectivesConfig objectives
) {
	public static final Codec<PyriteMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PyriteMapMetadata.MAP_CODEC.forGetter(PyriteMap::metadata),
			PyriteMapGenerator.CODEC.fieldOf("generator").forGetter(PyriteMap::generator),
			PlayerConfig.CODEC.fieldOf("player_config").forGetter(PyriteMap::playerConfig),
			GameTeamList.CODEC.fieldOf("teams").forGetter(PyriteMap::teams),
			KeyableList.codec(Region.CODEC).fieldOf("regions").orElse(KeyableList.empty()).forGetter(PyriteMap::regions),
			KeyableList.codec(Spawn.CODEC).fieldOf("spawns").orElse(KeyableList.empty()).forGetter(PyriteMap::spawns),
			RespawnSettings.CODEC.fieldOf("respawn").orElse(RespawnSettings.DEFAULT).forGetter(PyriteMap::respawnSettings),
			KeyableList.codec(PyritePredicate.CODEC).fieldOf("predicates").orElse(KeyableList.empty()).forGetter(PyriteMap::predicates),
			PlayerListenerLists.CODEC.fieldOf("player_listeners").forGetter(PyriteMap::playerListenerLists),
			KeyableList.codec(Kit.CODEC).fieldOf("kits").orElse(KeyableList.empty()).forGetter(PyriteMap::kits),
			ObjectivesConfig.CODEC.fieldOf("objectives").forGetter(PyriteMap::objectives)
	).apply(instance, PyriteMap::new));

	public GameTeam team(GameTeamKey key) {
		var team = this.teams.byKey(key);
		if(team == null) throw new GameOpenException(Text.literal("Could not find team with key '" + key + "'"));
		return team;
	}

	public Region region(String key) {
		var region = this.regions.byKey(key);
		if(region == null) throw new GameOpenException(Text.literal("Could not find region with key '" + key + "'"));
		return region;
	}

	public Spawn spawn(String key) {
		var spawn = this.spawns.byKey(key);
		if(spawn == null) throw new GameOpenException(Text.literal("Could not find spawn with key '" + key + "'"));
		return spawn;
	}

	public PyritePredicate predicate(String key) {
		var predicate = this.predicates.byKey(key);
		if(predicate == null) throw new GameOpenException(Text.literal("Could not find predicates with key '" + key + "'"));
		return predicate;
	}

	public Kit kit(String key) {
		var kit = this.kits.byKey(key);
		if(kit == null) throw new GameOpenException(Text.literal("Could not find kit with key '" + key + "'"));
		return kit;
	}

	public Spawn getDefaultSpawn() {
		return this.spawn("default");
	}
}
