package com.kuuhaku.entities.base;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Coordinates;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Sprite {
	private final GameRuntime runtime;
	private final String name;
	private final int gridX, gridY, delay;
	private final boolean loop;

	private Coordinates coordinates;
	private long start;
	private int track = -1;

	public Sprite(GameRuntime runtime, String name) {
		this.runtime = runtime;
		this.name = name;
		this.gridX = 1;
		this.gridY = 1;
		this.delay = (int) runtime.getFPS();
		this.loop = false;
	}

	public Sprite(GameRuntime runtime, String name, int gridX, int gridY, int delay, boolean loop) {
		this.runtime = runtime;
		this.name = name;
		this.gridX = gridX;
		this.gridY = gridY;
		this.delay = delay;
		this.loop = loop;
	}

	public boolean hasImage() {
		return name != null && !name.isBlank();
	}

	public BufferedImage getImage() {
		BufferedImage source = AssetManager.getSprite(name);
		if (source == null) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		} else if (gridX == 1 && gridY == 1) {
			return source;
		}

		Rectangle crop = new Rectangle(source.getWidth() / gridX, source.getHeight() / gridY);

		if (track >= 0) {
			int frame = getFrame() % gridX;
			return source.getSubimage(
					crop.width * (frame % gridX), crop.height * track,
					crop.width, crop.height
			);
		} else if (track == -1) {
			int frame = getFrame();
			return source.getSubimage(
					crop.width * (frame % gridX), crop.height * (frame / gridX),
					crop.width, crop.height
			);
		} else {
			return null;
		}
	}

	public Coordinates getBounds() {
		if (coordinates == null) {
			if (hasImage()) {
				BufferedImage sprite = getImage();
				coordinates = new Coordinates(new Rectangle(sprite.getWidth(), sprite.getHeight()));
			} else {
				coordinates = new Coordinates();
			}
		}

		return coordinates;
	}

	public int getFrame() {
		if (start == 0) start = runtime.getTick();

		if (loop) {
			return (int) ((runtime.getTick() - start) / delay) % gridX * gridY;
		} else {
			return (int) Math.min((runtime.getTick() - start) / delay, (long) gridX * gridY - 1);
		}
	}

	public void setTrack(int track) {
		this.track = track;
	}
}
