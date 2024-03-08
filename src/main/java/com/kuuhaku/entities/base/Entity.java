package com.kuuhaku.entities.base;

import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Coordinates;
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
	private boolean cullable, disposed, spawned;

	private Entity parent;
	private final Set<Entity> children = new HashSet<>();

	public Entity(GameRuntime runtime, Entity parent) {
		this(runtime, parent, null);
	}

	public Entity(GameRuntime runtime, Entity parent, Sprite sprite) {
		this.runtime = runtime;

		if (this instanceof IParticle) {
			this.sprite = new Sprite(runtime, null);
		} else {
			Metadata info = getClass().getDeclaredAnnotation(Metadata.class);
			if (info == null) {
				this.sprite = sprite;
			} else {
				this.sprite = new Sprite(runtime, info.sprite());
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

	public void setParent(Entity parent) {
		getCoordinates().setParent(parent == null ? null : parent.getCoordinates());
		if (this.parent != null) {
			this.parent.children.remove(this);
		}
		this.parent = parent;
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

	public float[] getAnchor() {
		return getCoordinates().getAnchor();
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
		float[] anchor = getAnchor();

		AffineTransform at = AffineTransform.getTranslateInstance(pos[0], pos[1]);
		at.translate(-anchor[0], -anchor[1]);
		at.rotate(getAngle(), anchor[0], anchor[1]);

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

		if (parent != null) {
			parent.getChildren().remove(this);
		}

		for (Entity child : children) {
			child.disposed = true;
		}
	}

	public boolean isDisposed() {
		return disposed;
	}

	public boolean toBeRemoved() {
		return disposed
			   || (this instanceof IDamageable d && d.getHp() <= 0)
			   || !getCoordinates().intersect(runtime.getBounds());
	}

	public boolean wasSpawned() {
		return spawned;
	}

	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
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
