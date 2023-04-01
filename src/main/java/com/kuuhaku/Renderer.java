package com.kuuhaku;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Renderer extends Canvas {
	private final JFrame window = new JFrame();
	private final Font font = new Font("Monospaced", Font.PLAIN, 15);
	private Consumer<Graphics2D> frame;
	private long lastFrame;
	private double framerate;

	public Renderer(double fps) {
		window.setSize(800, 600);
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.add(this);
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		createBufferStrategy(2);

		framerate = 1000 / fps;
		Thread render = new Thread(() -> {
			while (!Thread.interrupted()) {
				if (frame != null) {
					frame.accept((Graphics2D) getBufferStrategy().getDrawGraphics());
					getBufferStrategy().show();
				}

				lastFrame = System.currentTimeMillis();

				try {
					Thread.sleep((long) framerate, (int) (1_000_000 * (framerate - (long) framerate)));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		render.setDaemon(true);
		render.start();
	}

	public synchronized void render(Consumer<Graphics2D> act) {
		frame = act;
	}

	public JFrame getWindow() {
		return window;
	}

	public double getFramerate() {
		return framerate;
	}

	public long getFrameTime() {
		return System.currentTimeMillis() - lastFrame;
	}

	@Override
	public Font getFont() {
		return font;
	}
}
