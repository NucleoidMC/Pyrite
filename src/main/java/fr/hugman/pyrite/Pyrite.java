package fr.hugman.pyrite;

import com.google.common.reflect.Reflection;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Pyrite implements ModInitializer {
	private static final String MOD_ID = "pyrite";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static Identifier id(String s) {
		return new Identifier(MOD_ID, s);
	}

	@Override
	public void onInitialize() {
		Reflection.initialize(PyriteRegistries.class);
		//GameType.register(Pyrite.id("base"), PyriteGameConfig.CODEC, PyriteWaiting::open);
	}
}
