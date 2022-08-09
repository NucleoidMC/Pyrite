package fr.hugman.pyrite.map.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.codecs.MoreCodecs;

import java.util.List;

public record PlayerListenerLists(
		List<Listener> move,
		List<Listener> enterRegion,
		List<Listener> leaveRegion,
		List<Listener> placeBlock,
		List<Listener> breakBlock,
		List<Listener> useBlock
) {
	public static final Codec<PlayerListenerLists> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("move").orElse(List.of()).forGetter(PlayerListenerLists::move),
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("enter_region").orElse(List.of()).forGetter(PlayerListenerLists::enterRegion),
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("leave_region").orElse(List.of()).forGetter(PlayerListenerLists::leaveRegion),
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("place_block").orElse(List.of()).forGetter(PlayerListenerLists::placeBlock),
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("break_block").orElse(List.of()).forGetter(PlayerListenerLists::breakBlock),
			MoreCodecs.listOrUnit(Listener.CODEC).fieldOf("use_block").orElse(List.of()).forGetter(PlayerListenerLists::useBlock)
	).apply(instance, PlayerListenerLists::new));
}
