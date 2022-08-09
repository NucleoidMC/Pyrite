package fr.hugman.pyrite.game;

import fr.hugman.pyrite.util.TickUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.common.widget.SidebarWidget;

public record PyriteSidebar(PyriteGame game, SidebarWidget widget) {
	public static PyriteSidebar create(PyriteGame game, GlobalWidgets widgets) {
		Text title = game.space().getMetadata().sourceConfig().name().copy().formatted(Formatting.AQUA);
		return new PyriteSidebar(game, widgets.addSidebar(title));
	}

	public void update(long gameTick) {
		widget.set(content -> {
			content.add(Text.empty());
			game.playerManager().progressManager().scoreProgress().ifPresent(scoreObjective -> scoreObjective.fillSidebar(this.game, content));
			content.add(Text.empty());
			content.add(TickUtil.toText(gameTick));
		});
	}
}