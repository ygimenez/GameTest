package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.ui.ButtonElement;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Game extends JFrame implements IMenu {
	private final Renderer renderer = new Renderer();

	@Override
	public void switchTo(IMenu from) {
		ButtonElement play = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("PLAY");

		ButtonElement train = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("TRAINING");

		ButtonElement settings = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("SETTINGS");

		ButtonElement close = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("EXIT");

		play.addListener(e -> {
			new GameRuntime(renderer, null).switchTo(this);
			play.dispose();
			train.dispose();
			settings.dispose();
			close.dispose();
		});
		train.addListener(e -> {
			new Training(renderer).switchTo(this);
			play.dispose();
			train.dispose();
			settings.dispose();
			close.dispose();
		});
		settings.addListener(e -> {
			new Settings(renderer).switchTo(this);
			play.dispose();
			train.dispose();
			settings.dispose();
			close.dispose();
		});
		close.addListener(e -> System.exit(0));

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			g2d.setColor(Color.WHITE);
			g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 80));
			Utils.drawAlignedString(g2d, "SPACE BREACH", renderer.getWidth() / 2, 100, Utils.ALIGN_CENTER);

			int i = 0;
			for (ButtonElement btn : List.of(play, train, settings, close)) {
				btn.render(g2d, renderer.getWidth() / 2 - btn.getWidth() / 2, renderer.getHeight() / 2 - 50 + 60 * i++);
			}

			g2d.setFont(renderer.getFont());
			Utils.drawAlignedString(g2d, "v0.0.1-ALPHA", renderer.getWidth() - 10, renderer.getHeight() - 20, Utils.ALIGN_LEFT);
		});
	}
}
