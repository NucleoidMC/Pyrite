package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.kit.Kit;
import fr.hugman.pyrite.map.listener.RegionListeners;
import fr.hugman.pyrite.map.predicate.PyritePredicate;
import fr.hugman.pyrite.map.region.Region;
import fr.hugman.pyrite.map.spawn.RespawnSettings;
import fr.hugman.pyrite.map.spawn.Spawn;
import fr.hugman.pyrite.util.KeyableList;
import xyz.nucleoid.plasmid.game.common.team.GameTeamList;

import java.util.List;

public record PyriteMap(
		PyriteMapMetadata metadata,
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
			GameTeamList.CODEC.fieldOf("teams").forGetter(PyriteMap::teams),
			KeyableList.codec(Region.CODEC).fieldOf("regions").forGetter(PyriteMap::regions),
			KeyableList.codec(Spawn.CODEC).fieldOf("spawns").forGetter(PyriteMap::spawns),
			KeyableList.codec(PyritePredicate.CODEC).fieldOf("predicates").forGetter(PyriteMap::predicates),
			RegionListeners.LIST_CODEC.fieldOf("listeners").forGetter(PyriteMap::listeners),
			KeyableList.codec(Kit.CODEC).fieldOf("kits").forGetter(PyriteMap::kits),
			RespawnSettings.CODEC.fieldOf("respawn").forGetter(PyriteMap::respawnSettings)
	).apply(instance, PyriteMap::new));
}
