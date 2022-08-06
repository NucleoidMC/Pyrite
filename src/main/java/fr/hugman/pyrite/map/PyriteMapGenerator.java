package fr.hugman.pyrite.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.MapTemplateSerializer;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

import java.io.IOException;

public record PyriteMapGenerator(Identifier templateId, boolean daylightCycle, RegistryEntry<DimensionType> dimensions) {
	//TODO: implement map generator types (i.e. for procedural generation)

	public static final Codec<PyriteMapGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("template").forGetter(PyriteMapGenerator::templateId),
			Codec.BOOL.fieldOf("daylight_cycle").forGetter(PyriteMapGenerator::daylightCycle),
			DimensionType.REGISTRY_CODEC.fieldOf("dimension").forGetter(PyriteMapGenerator::dimensions)
	).apply(instance, PyriteMapGenerator::new));

	public ChunkGenerator getChunkGenerator(MinecraftServer server) {
		try {
			return new TemplateChunkGenerator(server, MapTemplateSerializer.loadFromResource(server, this.templateId));
		} catch(IOException e) {
			throw new GameOpenException(Text.translatable("error.pyrite.map.generator.load_template"), e);
		}
	}
}