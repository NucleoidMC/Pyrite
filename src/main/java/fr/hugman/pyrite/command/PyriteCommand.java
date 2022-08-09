package fr.hugman.pyrite.command;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.PyriteRegistries;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.data.DataProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;

import java.io.IOException;
import java.io.StringWriter;

public class PyriteCommand {
	private static final String NAME = Pyrite.MOD_ID;
	private static final String MAP_ARGUMENT = "map";
	private static final DynamicCommandExceptionType INVALID_MAP_EXCEPTION = new DynamicCommandExceptionType((id) -> Text.translatable("commands.pyrite.map.invalid", id));
	private static final SimpleCommandExceptionType MAP_INFO_DECODE_FAIL = new SimpleCommandExceptionType(Text.translatable("commands.pyrite.map.info.failed.decode"));
	private static final SimpleCommandExceptionType MAP_INFO_DISPLAY_FAIL = new SimpleCommandExceptionType(Text.translatable("commands.pyrite.map.info.failed.display"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(CommandManager.literal(NAME)
				.then(CommandManager.literal("map")
						.then(CommandManager.literal("data")
								.then(CommandManager.argument(MAP_ARGUMENT, RegistryKeyArgumentType.registryKey(PyriteRegistries.MAP.getKey()))
										.executes(context -> mapInfo(context.getSource(), getMapEntry(context, MAP_ARGUMENT)))))));
	}

	private static RegistryEntry<PyriteMap> getMapEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		RegistryKey<?> unknownRegistryKey = context.getArgument(name, RegistryKey.class);
		RegistryKey<PyriteMap> registryKey = unknownRegistryKey.tryCast(PyriteRegistries.MAP.getKey()).orElseThrow(() -> INVALID_MAP_EXCEPTION.create(unknownRegistryKey));
		return context.getSource().getServer().getRegistryManager().get(PyriteRegistries.MAP.getKey()).getEntry(registryKey).orElseThrow(() -> INVALID_MAP_EXCEPTION.create(registryKey.getValue()));
	}

	private static int mapInfo(ServerCommandSource source, RegistryEntry<PyriteMap> map) throws CommandSyntaxException {
		var dataResult = PyriteMap.CODEC.encodeStart(JsonOps.INSTANCE, map.value());
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
