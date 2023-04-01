package com.kuuhaku.view;

import com.kuuhaku.LeaderboardsManager;
import com.kuuhaku.Renderer;
import com.kuuhaku.Utils;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.ui.ButtonElement;

import java.awt.*;
import java.util.Map;

public class Leaderboards implements IMenu {
	private final Renderer renderer;

	private int scroll;

	public Leaderboards(Renderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void switchTo(IMenu from) {
		ButtonElement back = new ButtonElement(renderer)
				.setSize(150, 50)
				.setText("BACK");

		back.addListener(e -> {
			from.switchTo(this);
			back.dispose();
		});

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			g2d.setColor(Color.WHITE);
			g2d.setFont(renderer.getFont().deriveFont(Font.PLAIN, 30));
			FontMetrics fm = g2d.getFontMetrics();

			int i = 0;
			for (Map.Entry<String, Integer> score : LeaderboardsManager.getScores()) {
				if (i++ >= 10) break;

				Utils.drawAlignedString(g2d, score.getKey() + " - " + score.getValue(),
						renderer.getWidth() / 2,
						renderer.getHeight() / 2 + fm.getHeight() * (i - 5),
						Utils.ALIGN_CENTER
				);
			}

			back.render(g2d, 10, 10);
		});
	}
}
