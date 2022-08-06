package fr.hugman.pyrite.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

import java.util.List;

public record PyriteGameConfig(PlayerConfig players, List<Identifier> maps) {
	public static final Codec<PyriteGameConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PlayerConfig.CODEC.fieldOf("players").forGetter(PyriteGameConfig::players),
			Identifier.CODEC.listOf().fieldOf("maps").forGetter(PyriteGameConfig::maps)
	).apply(instance, PyriteGameConfig::new));
}
