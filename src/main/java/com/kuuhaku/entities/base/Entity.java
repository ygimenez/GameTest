package com.kuuhaku.entities.base;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Coordinates;
import com.kuuhaku.view.GameRuntime;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity {
	private final int id = ThreadLocalRandom.current().nextInt();
	private final BufferedImage sprite;
	private final Coordinates bounds;
	private int hp;

	public Entity(String sprite, int hp) {
		BufferedImage img = AssetManager.getSprite(sprite);
		if (img == null) {
			this.sprite = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			bounds = new Coordinates();
			return;
		}

		this.sprite = img;
		bounds = new Coordinates(this.sprite.getData().getBounds());
		this.hp = hp;
	}

	public abstract GameRuntime getParent();

	public BufferedImage getSprite() {
		return sprite;
	}

	public Coordinates getBounds() {
		return bounds;
	}

	public int getX() {
		return bounds.getX();
	}

	public int getY() {
		return bounds.getY();
	}

	public int getWidth() {
		return bounds.getWidth();
	}

	public int getHeight() {
		return bounds.getHeight();
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = Math.max(0, hp);
	}

	public void destroy() {

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Entity entity = (Entity) o;
		return id == entity.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
