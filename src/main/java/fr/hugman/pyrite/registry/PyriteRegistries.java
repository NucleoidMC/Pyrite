package fr.hugman.pyrite.registry;

import com.google.common.reflect.Reflection;
import fr.hugman.pyrite.Pyrite;
import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.point.PointProviderType;
import fr.hugman.pyrite.map.predicate.PyritePredicateType;
import fr.hugman.pyrite.map.region.RegionType;
import fr.hugman.pyrite.map.trigger.PyriteTriggerType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.SimpleRegistry;

public class PyriteRegistries {
	public static final SimpleRegistry<PointProviderType> POINT_PROVIDER_TYPE = FabricRegistryBuilder.createSimple(PointProviderType.class, Pyrite.id("point_provider_type")).buildAndRegister();
	public static final SimpleRegistry<PyritePredicateType> PYRITE_PREDICATE_TYPE = FabricRegistryBuilder.createSimple(PyritePredicateType.class, Pyrite.id("predicate_type")).buildAndRegister();
	public static final SimpleRegistry<RegionType> REGION_TYPE = FabricRegistryBuilder.createSimple(RegionType.class, Pyrite.id("region_type")).buildAndRegister();
	public static final SimpleRegistry<PyriteTriggerType> TRIGGER_TYPE = FabricRegistryBuilder.createSimple(PyriteTriggerType.class, Pyrite.id("trigger_type")).buildAndRegister();
	public static final ReloadableResourceManager<PyriteMap> MAP = ReloadableResourceManager.of(PyriteMap.CODEC, "map");

	public static void register() {
		MAP.register(Pyrite.id("map"));
		Reflection.initialize(PointProviderType.class);
		Reflection.initialize(PyritePredicateType.class);
		Reflection.initialize(RegionType.class);
		Reflection.initialize(PyriteTriggerType.class);
		Reflection.initialize(PyriteMap.class);
	}
}
