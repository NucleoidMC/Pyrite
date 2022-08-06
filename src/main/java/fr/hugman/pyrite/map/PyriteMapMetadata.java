package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.util.PyriteDate;
import net.minecraft.text.Text;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.List;

public record PyriteMapMetadata(
		Text name,
		String version,
		Text description,
		PyriteDate created,
		List<PyriteMapAuthor> authors
) {
	public static final MapCodec<PyriteMapMetadata> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PlasmidCodecs.TEXT.fieldOf("name").forGetter(PyriteMapMetadata::name),
			Codec.STRING.fieldOf("version").forGetter(PyriteMapMetadata::version),
			PlasmidCodecs.TEXT.fieldOf("description").forGetter(PyriteMapMetadata::description),
			PyriteDate.CODEC.fieldOf("created").forGetter(PyriteMapMetadata::created),
			PyriteMapAuthor.CODEC.listOf().fieldOf("authors").forGetter(PyriteMapMetadata::authors)
	).apply(instance, PyriteMapMetadata::new));
}
