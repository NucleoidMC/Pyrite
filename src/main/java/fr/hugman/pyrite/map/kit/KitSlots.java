package fr.hugman.pyrite.map.kit;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;

import java.util.Map;

public record KitSlots(Map<String, ItemStack> slots) {
	public static final Codec<KitSlots> CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC).xmap(KitSlots::new, KitSlots::slots);
}
