package com.kuuhaku;

import com.kuuhaku.enums.ScreenMode;
import com.kuuhaku.enums.ScreenSize;
import com.kuuhaku.manager.SettingsManager;
import com.kuuhaku.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Renderer extends Canvas {
	private final JFrame window = new JFrame();
	private final GraphicsDevice device = window.getGraphicsConfiguration().getDevice();
	private final Font font = new Font("Monospaced", Font.PLAIN, 15);
	private final ScreenSize resolution = ScreenSize.R_800x600;
	private AtomicReference<Consumer<Graphics2D>> frame = new AtomicReference<>(null);
	private long lastFrame, frameTime;
	private float framerate;

	public Renderer() {
		window.setTitle("Space Breach - v0.0.1f-ALPHA");
		window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		window.add(this);
		window.setLocationRelativeTo(null);
		window.setResizable(false);
		window.setVisible(true);
		createBufferStrategy(2);

		framerate = 1000f / Integer.parseInt(SettingsManager.get("framerate", "60"));
		Thread render = new Thread(() -> {
			while (true) {
				if (frame.get() != null && window.isVisible()) {
					frame.get().accept((Graphics2D) getBufferStrategy().getDrawGraphics());
					getBufferStrategy().show();
				}

				frameTime = System.currentTimeMillis() - lastFrame;
				lastFrame = System.currentTimeMillis();

				Utils.sleep((long) framerate, (int) (1_000_000 * (framerate - (long) framerate)));
			}
		});
		render.setDaemon(true);
		render.start();

		updateScreenMode(ScreenMode.valueOf(SettingsManager.get("window_mode", ScreenMode.WINDOWED.name()).toUpperCase()));
	}

	public synchronized void render(Consumer<Graphics2D> act) {
		frame.set(act);
	}

	public GraphicsDevice getDevice() {
		return device;
	}

	public void updateScreenMode(ScreenMode mode) {
		window.dispose();

		switch (mode) {
			case WINDOWED -> {
				device.setFullScreenWindow(null);
				window.setExtendedState(JFrame.NORMAL);
				window.setUndecorated(false);

				ScreenSize size = ScreenSize.valueOf("R_" + SettingsManager.get("window_size", ScreenSize.R_800x600.name().substring(2)));
				window.setBounds(size.getBounds());
				window.setLocationRelativeTo(null);
			}
			case BORDERLESS -> {
				window.setExtendedState(JFrame.MAXIMIZED_BOTH);
				window.setUndecorated(true);
			}
		}

		window.setVisible(true);
	}

	public JFrame getWindow() {
		return window;
	}

	public float getFramerate() {
		return framerate;
	}

	public long getFrameTime() {
		return frameTime;
	}

	@Override
	public Font getFont() {
		return font;
	}

	public ScreenSize getResolution() {
		return resolution;
	}

	public void updateSettings() {
		framerate = 1000f / Integer.parseInt(SettingsManager.get("framerate", "60"));
	}
}
