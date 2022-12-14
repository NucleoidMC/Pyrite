package fr.hugman.pyrite.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import fr.hugman.pyrite.PyriteRegistries;
import fr.hugman.pyrite.map.PyriteMap;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DynamicRegistryManager.class)
public interface DynamicRegistryManagerMixin {
	@Shadow
	private static <E> void register(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> infosBuilder, RegistryKey<? extends Registry<E>> registryRef, Codec<E> entryCodec) {}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(method = "method_30531", at = @At("STORE"))
	private static ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> addInfos(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, DynamicRegistryManager.Info<?>> builder) {
		register(builder, PyriteRegistries.MAP.getKey(), PyriteMap.CODEC);
		return builder;
	}
}
