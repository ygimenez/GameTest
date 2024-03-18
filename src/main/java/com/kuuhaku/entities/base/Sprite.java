package com.kuuhaku.entities.base;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Coordinates;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class Sprite {
	private final GameRuntime runtime;
	private final String name;
	private final int gridX, gridY, delay, offset;
	private final boolean loop;

	private Coordinates coordinates;
	private long start;
	private int track = -1;
	private Color color;
	private float[] scale = {1, 1};

	private BufferedImage cached;

	public Sprite(GameRuntime runtime, String name) {
		this.runtime = runtime;
		this.name = name;
		this.gridX = 1;
		this.gridY = 1;
		this.delay = (int) runtime.getFPS();
		this.loop = false;
		this.offset = 0;
		color = runtime.getForeground();
	}

	public Sprite(GameRuntime runtime, String name, int gridX, int gridY, int delay, int offset, boolean loop) {
		this.runtime = runtime;
		this.name = name;
		this.gridX = gridX;
		this.gridY = gridY;
		this.delay = delay;
		this.loop = loop;
		this.offset = offset;
		color = runtime.getForeground();
	}

	public boolean hasImage() {
		return name != null && !name.isBlank();
	}

	public BufferedImage getImage() {
		BufferedImage source = cached;
		if (source == null) {
			BufferedImage sample = applyColor(AssetManager.getSprite(name));
			if (sample != null) {
				source = new BufferedImage((int) (sample.getWidth() * scale[0]), (int) (sample.getHeight() * scale[1]), sample.getType());

				Graphics2D g2d = source.createGraphics();
				g2d.drawImage(sample, 0, 0, source.getWidth(), source.getHeight(), null);
				g2d.dispose();

				cached = source;
			}
		}

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

	private BufferedImage applyColor(BufferedImage image) {
		if (image == null) return null;

		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = out.createGraphics();
		g2d.drawImage(image, 0, 0, null);

		g2d.setComposite(AlphaComposite.SrcIn);
		g2d.setPaint(color);
		g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

		g2d.dispose();

		return out;
	}

	public Coordinates getBounds() {
		if (coordinates == null) {
			recalculateBounds();
		}

		return coordinates;
	}

	public int getFrame() {
		if (start == 0) start = runtime.getTick();

		if (loop) {
			return (int) ((runtime.getTick() - start) / delay + offset) % gridX * gridY;
		} else {
			return (int) Math.min((runtime.getTick() - start) / delay + offset, (long) gridX * gridY - 1);
		}
	}

	public void setTrack(int track) {
		this.track = track;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		this.cached = null;
	}

	public float[] getScale() {
		return scale;
	}

	public void setScale(float w, float h) {
		this.scale = new float[]{w, h};
		this.cached = null;
		recalculateBounds();
	}

	public boolean hasFinished() {
		return getFrame() == gridX * gridY - 1;
	}

	private void recalculateBounds() {
		if (hasImage()) {
			BufferedImage sprite = getImage();
			assert sprite != null;

			Rectangle rect = new Rectangle(sprite.getWidth(), sprite.getHeight());
			if (coordinates != null) {
				rect.setBounds((int) coordinates.getPosition()[0], (int) coordinates.getPosition()[1], rect.width, rect.height);
			}

			coordinates = new Coordinates(rect);
		} else {
			coordinates = new Coordinates();
		}
	}
}
