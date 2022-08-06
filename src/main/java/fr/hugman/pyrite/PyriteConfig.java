package fr.hugman.pyrite;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.List;

public record PyriteConfig(List<Identifier> maps) {
	public static final Codec<PyriteConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.listOf().fieldOf("maps").forGetter(PyriteConfig::maps)
	).apply(instance, PyriteConfig::new));
}
