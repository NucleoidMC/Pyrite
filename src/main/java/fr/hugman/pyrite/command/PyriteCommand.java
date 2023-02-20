package fr.hugman.pyrite.command;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.JsonOps;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.registry.PyriteRegistries;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.data.DataProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.StringWriter;

public class PyriteCommand {
	private static final String NAME = Pyrite.MOD_ID;
	private static final String MAP_ARGUMENT = "map";
	private static final DynamicCommandExceptionType INVALID_MAP_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("commands.pyrite.map.invalid", id));
	private static final SimpleCommandExceptionType MAP_INFO_DECODE_FAIL = new SimpleCommandExceptionType(Text.translatable("commands.pyrite.map.info.failed.decode"));
	private static final SimpleCommandExceptionType MAP_INFO_DISPLAY_FAIL = new SimpleCommandExceptionType(Text.translatable("commands.pyrite.map.info.failed.display"));
	public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(PyriteRegistries.MAP.getIds(), builder);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal(NAME)
				.then(CommandManager.literal("map")
						.then(CommandManager.literal("data")
								.then(CommandManager.argument(MAP_ARGUMENT, IdentifierArgumentType.identifier()).suggests(SUGGESTION_PROVIDER)
										.executes(context -> sendRawMapData(context.getSource(), IdentifierArgumentType.getIdentifier(context, MAP_ARGUMENT)))))));
	}

	private static int sendRawMapData(ServerCommandSource source, Identifier id) throws CommandSyntaxException {
		var map = PyriteRegistries.MAP.get(id);
		if(map == null) {
			throw INVALID_MAP_EXCEPTION.create(id);
		}
		var dataResult = PyriteMap.CODEC.encodeStart(JsonOps.INSTANCE, map);
		if(dataResult.result().isEmpty()) {
			throw MAP_INFO_DECODE_FAIL.create();
		}
		try {
			source.sendFeedback(Text.literal(toPrettyString(dataResult.result().get())), true);
			return 1;
		} catch(AssertionError e) {
			throw MAP_INFO_DISPLAY_FAIL.create();
		}
	}

	public static String toPrettyString(JsonElement element) {
		try {
			StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			jsonWriter.setSerializeNulls(false);
			jsonWriter.setIndent("  ");
			JsonHelper.writeSorted(jsonWriter, element, DataProvider.JSON_KEY_SORTING_COMPARATOR);
			Streams.write(element, jsonWriter);
			jsonWriter.close();
			return stringWriter.toString();
		} catch(IOException var3) {
			throw new AssertionError(var3);
		}
	}
}
