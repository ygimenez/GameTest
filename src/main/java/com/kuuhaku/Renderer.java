package com.kuuhaku;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Renderer extends Canvas {
	private final Font font = new Font("Monospaced", Font.PLAIN, 15);

	public Renderer() {
		JFrame window = new JFrame();
		window.setSize(800, 600);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.add(this);
		window.setVisible(true);

		createBufferStrategy(2);
	}

	public void render(Consumer<Graphics2D> act) {
		act.accept((Graphics2D) getBufferStrategy().getDrawGraphics());
		getBufferStrategy().show();
	}

	@Override
	public Font getFont() {
		return font;
	}
}
