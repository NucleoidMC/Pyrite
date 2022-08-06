package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import xyz.nucleoid.codecs.MoreCodecs;
import xyz.nucleoid.plasmid.util.PlasmidCodecs;

import java.util.Optional;
import java.util.UUID;

public record PyriteMapAuthor(
		UUID uuid,
		String name,
		Optional<Text> contribution
) {
	public static final Codec<PyriteMapAuthor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MoreCodecs.UUID_STRING.fieldOf("uuid").forGetter(PyriteMapAuthor::uuid),
			Codec.STRING.fieldOf("name").forGetter(PyriteMapAuthor::name),
			PlasmidCodecs.TEXT.optionalFieldOf("contribution").forGetter(PyriteMapAuthor::contribution)
	).apply(instance, PyriteMapAuthor::new));
}
