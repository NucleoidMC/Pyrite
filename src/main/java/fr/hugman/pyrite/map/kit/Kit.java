package fr.hugman.pyrite.map.kit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record Kit(Optional<String> parent, KitSlots slots) {
	public static final Codec<Kit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("parent").forGetter(Kit::parent),
			KitSlots.CODEC.fieldOf("slots").forGetter(Kit::slots)
	).apply(instance, Kit::new));
}
