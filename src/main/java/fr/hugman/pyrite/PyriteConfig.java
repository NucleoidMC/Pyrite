package fr.hugman.pyrite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.registry.PyriteRegistries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.List;

/**
 * Main configuration of a game mode.
 *
 * @param maps the list of maps that can be loaded
 *
 * @author Hugman
 * @see PyriteMap the main map configuration
 * @since 1.0.0
 */
public record PyriteConfig(List<Identifier> maps) {
	public static final Codec<PyriteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("maps").forGetter(PyriteConfig::maps)
	).apply(instance, PyriteConfig::new));

	public PyriteMap randomMap(Random random) {
		var maps = this.maps();
		var mapId = maps.get(random.nextInt(maps.size()));
		return PyriteRegistries.MAP.get(mapId);
	}
}
