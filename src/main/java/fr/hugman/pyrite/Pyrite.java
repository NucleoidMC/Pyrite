package fr.hugman.pyrite;

import com.google.common.reflect.Reflection;
import fr.hugman.pyrite.game.phase.PyriteWaiting;
import fr.hugman.pyrite.map.predicate.PyritePredicateType;
import fr.hugman.pyrite.map.region.RegionType;
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
		Reflection.initialize(PyriteRegistries.class);
		Reflection.initialize(PyritePredicateType.class);
		Reflection.initialize(RegionType.class);
		GameType.register(Pyrite.id("base"), PyriteConfig.CODEC, PyriteWaiting::open);
	}
}
