package fr.hugman.pyrite.map.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.hugman.pyrite.context.EventContext;
import fr.hugman.pyrite.map.objective.progress.ScoreProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.intprovider.IntProvider;
import xyz.nucleoid.plasmid.game.common.team.GameTeamKey;

import java.util.Optional;

public record ScorePyriteTrigger(GameTeamKey teamKey, IntProvider amount) implements PyriteTrigger {
	public static final Codec<ScorePyriteTrigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			GameTeamKey.CODEC.fieldOf("team").forGetter(ScorePyriteTrigger::teamKey),
			IntProvider.VALUE_CODEC.fieldOf("amount").forGetter(ScorePyriteTrigger::amount)
	).apply(instance, ScorePyriteTrigger::new));

	@Override
	public PyriteTriggerType<?> getType() {
		return PyriteTriggerType.SCORE;
	}

	@Override
	public ActionResult trigger(EventContext context) {
		int i = this.amount.get(context.game().random());
		if(context.thisEntity() != null && context.thisEntity() instanceof ServerPlayerEntity player) {
			if(i == 0) player.sendMessage(Text.literal("You haven't scored any point. Sad times. :("));
			else if(i == -1) context.game().sendMessage(Text.literal(player + " took a point from " + this.teamKey + "."));
			else if(i == 1) context.game().sendMessage(Text.literal(player + " score a point for " + this.teamKey + "."));
			else if(i < 0) context.game().sendMessage(Text.literal(player + " took " + i + " points from " + this.teamKey + "."));
			else context.game().sendMessage(Text.literal(player + " scored " + i + " point to " + this.teamKey + "."));
			context.game().tpToSpawn(player);
		}
		else {
			if(i == -1) context.game().sendMessage(Text.literal(this.teamKey + "lost a point."));
			else if(i == 1) context.game().sendMessage(Text.literal(this.teamKey + " gained a point."));
			else if(i < 0) context.game().sendMessage(Text.literal(this.teamKey + " lost " + i + " points."));
			else context.game().sendMessage(Text.literal(this.teamKey + " gained " + i + " points."));
		}
		//TODO: actually attribute the point
		Optional<ScoreProgress> optScore = context.game().playerManager().progressManager().scoreProgress();
		if(optScore.isEmpty()) throw new IllegalStateException("No score objective found");
		optScore.get().addPoint(this.teamKey, i);
		context.game().updateWinningStatus(this.teamKey);
		return ActionResult.PASS;
	}
}
