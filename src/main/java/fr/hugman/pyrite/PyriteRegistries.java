package fr.hugman.pyrite;

import fr.hugman.pyrite.map.PyriteMap;
import fr.hugman.pyrite.map.point.PointProviderType;
import fr.hugman.pyrite.map.predicate.PyritePredicateType;
import fr.hugman.pyrite.map.region.RegionType;
import fr.hugman.pyrite.map.trigger.PyriteTriggerType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.SimpleRegistry;

public class PyriteRegistries {
	public static final SimpleRegistry<PointProviderType> POINT_PROVIDER_TYPE = FabricRegistryBuilder.createSimple(PointProviderType.class, Pyrite.id("point_provider_type")).buildAndRegister();
	public static final SimpleRegistry<PyritePredicateType> PYRITE_PREDICATE_TYPE = FabricRegistryBuilder.createSimple(PyritePredicateType.class, Pyrite.id("predicate_type")).buildAndRegister();
	public static final SimpleRegistry<RegionType> REGION_TYPE = FabricRegistryBuilder.createSimple(RegionType.class, Pyrite.id("region_type")).buildAndRegister();
	public static final SimpleRegistry<PyriteTriggerType> TRIGGER_TYPE = FabricRegistryBuilder.createSimple(PyriteTriggerType.class, Pyrite.id("trigger_type")).buildAndRegister();
	public static final SimpleRegistry<PyriteMap> MAP = FabricRegistryBuilder.createSimple(PyriteMap.class, Pyrite.id("map")).buildAndRegister();
}
