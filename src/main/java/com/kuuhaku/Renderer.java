package com.kuuhaku;

import com.kuuhaku.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class Renderer extends Canvas {
	private final JFrame window = new JFrame();
	private final Font font = new Font("Monospaced", Font.PLAIN, 15);
	private Consumer<Graphics2D> frame;
	private long lastFrame, frameTime;
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

				frameTime = System.currentTimeMillis() - lastFrame;
				lastFrame = System.currentTimeMillis();

					Utils.sleep((long) framerate, (int) (1_000_000 * (framerate - (long) framerate)));
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
		return frameTime;
	}

	@Override
	public Font getFont() {
		return font;
	}
}
