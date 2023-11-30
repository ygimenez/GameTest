package com.kuuhaku.entities.base;

import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.interfaces.Metadata;
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
	private int hp, baseHp;
	private boolean cullable;
	private boolean disposed;

	private final Entity parent;
	private final Set<Entity> children = new HashSet<>();

	public Entity(GameRuntime runtime, Entity parent) {
		this(runtime, parent, null);
	}

	public Entity(GameRuntime runtime, Entity parent, Sprite sprite) {
		this.runtime = runtime;

		if (this instanceof IParticle) {
			this.sprite = new Sprite(runtime, null);
			this.hp = this.baseHp = 1;
		} else {
			Metadata info = getClass().getDeclaredAnnotation(Metadata.class);
			if (info == null) {
				this.sprite = sprite;
				this.hp = this.baseHp = 1;
			} else {
				this.sprite = new Sprite(runtime, info.sprite());
				this.hp = this.baseHp = (int) (info.hp() * (this instanceof Enemy ? runtime.getRound() / 5f : 1));
			}
		}

		this.parent = parent;
		if (parent != null) {
			getCoordinates().setParent(parent.getCoordinates());
			parent.children.add(this);
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
		if (parent != null && parent.getImage() == null) return null;

		return sprite.getImage();
	}

	public Coordinates getCoordinates() {
		return sprite.getBounds();
	}

	public float[] getPosition() {
		return getCoordinates().getPosition();
	}

	public Point2D.Float getCenter() {
		return getCoordinates().getCenter();
	}

	public Point2D.Float getGlobalCenter() {
		if (parent == null) return getCenter();

		float[] pos = getPosition();
		Point2D.Float ref = parent.getCenter();
		ref.setLocation(ref.x + pos[0], ref.y + pos[1]);

		return ref;
	}

	public int getWidth() {
		return getCoordinates().getWidth();
	}

	public int getHeight() {
		return getCoordinates().getHeight();
	}

	public float getAngle() {
		return getCoordinates().getAngle();
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

	public boolean isVisible() {
		if (parent != null) {
			return parent.isVisible();
		}

		return getCoordinates().getCollision().intersects(runtime.getSafeArea());
	}

	public Point2D.Float toLocal(int x, int y) {
		return toLocal(new Point2D.Float(x, y));
	}

	public Point2D.Float toLocal(Point2D.Float point) {
		float[] pos = getPosition();

		AffineTransform at = AffineTransform.getTranslateInstance(pos[0], pos[1]);
		at.rotate(getAngle(), getWidth() / 2f, getHeight() / 2f);

		return (Point2D.Float) at.transform(point, point);
	}

	public Set<Entity> getChildren() {
		return children;
	}

	public void calculateCoords() {
		getCoordinates().update();
	}

	public void onDestroy() {
	}

	public final void dispose() {
		disposed = true;
		for (Entity child : children) {
			child.disposed = true;
		}
	}

	public boolean toBeRemoved() {
		return disposed || getHp() <= 0 || !getCoordinates().intersect(runtime.getBounds());
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
