package fr.hugman.pyrite;

import fr.hugman.pyrite.game.phase.PyritePreStart;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.plasmid.game.GameType;

public class Pyrite implements ModInitializer {
	private static final String MOD_ID = "pyrite";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static Identifier id(String s) {
		return new Identifier(MOD_ID, s);
	}

	@Override
	public void onInitialize() {
		PyriteRegistries.onInitialize();
		GameType.register(Pyrite.id("base"), PyriteConfig.CODEC, PyritePreStart::open);
	}
}
