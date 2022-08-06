package fr.hugman.pyrite.map.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.PyriteEventContext;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;

public record TeamPyritePredicate(GameTeamKey teamKey) implements PyritePredicate {
	public static final Codec<TeamPyritePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			GameTeamKey.CODEC.fieldOf("team").forGetter(TeamPyritePredicate::teamKey)
	).apply(instance, TeamPyritePredicate::new));

	@Override
	public PyritePredicateType<?> getType() {
		return PyritePredicateType.ALWAYS;
	}

	@Override
	public boolean test(PyriteEventContext context) {
		if(context.thisEntity() instanceof ServerPlayerEntity player) {
			GameTeamKey key = context.game().teamKey(player);
			if(key == null) return false;
			return key.id().equals(teamKey.id());
		}
		return false;
	}
}