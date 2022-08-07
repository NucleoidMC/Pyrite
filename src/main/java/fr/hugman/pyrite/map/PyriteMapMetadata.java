package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.List;

/**
 * Mandatory metadata for a game map.
 * <p>
 * This may be ported to Plasmid in the future, so we can use it in lobby and on the website.
 *
 * @param name        the name of the map
 * @param version     the version of the map. Must follow the format "major.minor.patch"
 * @param description the short description of the map
 * @param created     the date the map was created. Must follow the format "dd/mm/yyyy"
 * @param authors     the authors and contributors of the map
 *
 * @author Hugman
 * @see PyriteMap#metadata() location of the metadata within the map
 * @since 1.0.0
 */
public record PyriteMapMetadata(
		Text name,
		String version,
		Text description,
		Date created,
		List<PyriteMapAuthor> authors
) {
	public static final MapCodec<PyriteMapMetadata> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PlasmidCodecs.TEXT.fieldOf("name").forGetter(PyriteMapMetadata::name),
			Codec.STRING.fieldOf("version").forGetter(PyriteMapMetadata::version),
			PlasmidCodecs.TEXT.fieldOf("description").forGetter(PyriteMapMetadata::description),
			Date.CODEC.fieldOf("created").forGetter(PyriteMapMetadata::created),
			PyriteMapAuthor.CODEC.listOf().fieldOf("authors").forGetter(PyriteMapMetadata::authors)
	).apply(instance, PyriteMapMetadata::new));

	public record Date(int day, int month, int year) {
		public static final Codec<Date> CODEC = Codec.STRING.xmap(Date::fromString, Date::toString);

		public static Date fromString(String s) {
			String[] split = s.split("/");
			return new Date(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		}

		public String toString() {
			return String.format("%d/%d/%d", day, month, year);
		}
	}
}
