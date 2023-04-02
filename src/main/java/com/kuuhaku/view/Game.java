package com.kuuhaku.view;

import com.kuuhaku.Renderer;
import com.kuuhaku.Utils;
import com.kuuhaku.interfaces.IMenu;
import com.kuuhaku.ui.ButtonElement;

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame implements IMenu {
	private final Renderer renderer = new Renderer(60);

	@Override
	public void switchTo(IMenu from) {
		ButtonElement play = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("PLAY");

		ButtonElement close = new ButtonElement(renderer)
				.setSize(250, 50)
				.setText("EXIT");

		play.addListener(e -> {
			new GameRuntime(renderer).switchTo(this);
			play.dispose();
			close.dispose();
		});
		close.addListener(e -> System.exit(0));

		renderer.render(g2d -> {
			g2d.setColor(Color.BLACK);
			g2d.fill(renderer.getBounds());

			g2d.setColor(Color.WHITE);
			g2d.setFont(renderer.getFont().deriveFont(Font.BOLD, 80));
			Utils.drawAlignedString(g2d, "SPACE BREACH", renderer.getWidth() / 2, 100, Utils.ALIGN_CENTER);

			play.render(g2d, renderer.getWidth() / 2 - close.getWidth() / 2, renderer.getHeight() / 2 - 50);
			close.render(g2d, renderer.getWidth() / 2 - close.getWidth() / 2, renderer.getHeight() / 2 + 10);

			g2d.setFont(renderer.getFont());
			Utils.drawAlignedString(g2d, "v0.0.1-ALPHA", renderer.getWidth() - 10, renderer.getHeight() - 20, Utils.ALIGN_RIGHT);
		});
	}
}
