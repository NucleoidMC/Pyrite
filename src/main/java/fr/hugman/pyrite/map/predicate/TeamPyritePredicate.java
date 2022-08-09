package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.context.EventEntitySelector;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;

public record TeamPyritePredicate(GameTeamKey teamKey, EventEntitySelector entitySelector) implements PyritePredicate {
	public static final Codec<TeamPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			GameTeamKey.CODEC.fieldOf("team").forGetter(TeamPyritePredicate::teamKey),
			EventEntitySelector.CODEC.fieldOf("entity").orElse(EventEntitySelector.THIS).forGetter(TeamPyritePredicate::entitySelector)
	).apply(instance, TeamPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.TEAM;
	}

	@Override
	public boolean test(EventContext context) {
		var entity = entitySelector.entity(context);
		if(context.game().playerManager() == null || entity == null) return false;
		if(entity instanceof ServerPlayerEntity player) {
			GameTeamKey key = context.game().playerManager().teamKey(player);
			if(key == null) return false;
			return key.id().equals(teamKey.id());
		}
		return false;
	}
}