package com.kuuhaku.entities.base;

import com.kuuhaku.utils.Coordinates;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Entity {
	private final GameRuntime runtime;
	private final int id = ThreadLocalRandom.current().nextInt();
	private final Sprite sprite;
	private boolean cullable;
	private int hp, baseHp;
	private boolean disposed;

	private final Entity parent;
	private final Set<Entity> children = new HashSet<>();

	public Entity(GameRuntime runtime) {
		this(runtime, null, new Sprite(runtime, null), 1);
	}

	public Entity(GameRuntime runtime, Entity parent, String sprite, int hp) {
		this(runtime, parent, new Sprite(runtime, sprite), hp);
	}

	public Entity(GameRuntime runtime, Entity parent, Sprite sprite, int hp) {
		this.runtime = runtime;
		this.sprite = sprite;
		this.hp = this.baseHp = hp;

		this.parent = parent;
		if (parent != null) {
			parent.children.add(this);
			getBounds().setReference(parent.getBounds());
		}
	}

	public GameRuntime getRuntime() {
		return runtime;
	}

	public Entity getParent() {
		return parent;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public BufferedImage getImage() {
		return sprite.getImage();
	}

	public Coordinates getBounds() {
		return sprite.getBounds();
	}

	public double[] getPosition() {
		return getBounds().getPosition();
	}

	public Point2D getCenter() {
		return getBounds().getCenter();
	}

	public int getWidth() {
		return getBounds().getWidth();
	}

	public int getHeight() {
		return getBounds().getHeight();
	}

	public double getAngle() {
		return getBounds().getAngle();
	}

	public int getBaseHp() {
		return baseHp;
	}

	public void setBaseHp(int baseHp) {
		this.baseHp = Math.max(0, baseHp);
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = Utils.clamp(hp, 0, baseHp);
	}

	public boolean isCullable() {
		return cullable;
	}

	public void setCullable(boolean cullable) {
		this.cullable = cullable;
	}

	public Point2D localToGlobal(int x, int y) {
		return localToGlobal(new Point2D.Double(x, y));
	}

	public Point2D localToGlobal(Point2D point) {
		double[] pos = getPosition();

		AffineTransform at = AffineTransform.getTranslateInstance(pos[0], pos[1]);
		at.rotate(getAngle(), getWidth() / 2d, getHeight() / 2d);

		return at.transform(point, point);
	}

	public Set<Entity> getChildren() {
		return children;
	}

	public void onDestroy() {
		disposed = true;
		for (Entity child : children) {
			child.disposed = true;
		}
	}

	public boolean toBeRemoved() {
		return disposed || hp <= 0 || !getBounds().intersect(runtime.getBounds());
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
