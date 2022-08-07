package fr.hugman.pyrite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;

import java.util.List;

/**
 * Main configuration of a game mode.
 * @param maps
 */
public record PyriteConfig(List<Identifier> maps) {
	public static final Codec<PyriteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("maps").forGetter(PyriteConfig::maps)
	).apply(instance, PyriteConfig::new));

	public PyriteMap randomMap(DynamicRegistryManager registryManager, Random random) {
		var maps = this.maps();
		var mapId = maps.get((int) Math.nextDown(maps.size()));
		return registryManager.get(PyriteRegistries.MAP.getKey()).get(mapId);
	}
}
