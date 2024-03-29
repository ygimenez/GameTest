package com.kuuhaku.entities.base;

import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;

public abstract class Projectile extends Entity implements IDynamic, ICollide {
	private final Entity source;
	private final int damage;
	private float speed;

	public Projectile(Entity source, int damage, float speed, float angle) {
		this(source, null, damage, speed, angle);
	}

	public Projectile(Entity source, Sprite sprite, int damage, float speed, float angle) {
		super(source.getRuntime(), null, sprite);
		this.source = source;
		this.speed = speed;
		this.damage = damage;

		Point2D.Float center = source.getGlobalCenter();
		getCoordinates().setAngle((float) (source.getAngle() + Math.toRadians(angle)));
		getCoordinates().setPosition(
				center.x - Utils.fsin(getAngle()) * source.getWidth(),
				center.y + Utils.fcos(getAngle()) * getHeight() / 2f + Utils.fcos(getAngle()) * source.getHeight()
		);
		getCoordinates().setAnchor(0.5f, 1);
	}

	@Override
	public void update() {
		move();
		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof IDamageable d && hit(entity)) {
				AssetManager.playCue("hit");
				d.damage(damage);
				dispose();
				break;
			}
		}
	}

	protected void move() {
		float[] vector = Utils.angToVec(getAngle());
		getCoordinates().translate(vector[0] * speed, vector[1] * speed);
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;

		return other instanceof Player != source instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}

	public int getDamage() {
		return damage;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Entity getSource() {
		return source;
	}

	public float[] getImpactNormal(Entity other) {
		Rectangle rect = getCoordinates().getCollision().getBounds();
		Rectangle col = other.getCoordinates().getCollision().getBounds();

		Line2D.Float line = new Line2D.Float();
		line.setLine(col.x, col.y, col.x + col.width, col.y);
		if (line.intersects(rect)) return new float[]{0, 1};

		line.setLine(col.x + col.width, col.y, col.x + col.width, col.y + col.height);
		if (line.intersects(rect)) return new float[]{-1, 0};

		line.setLine(col.x + col.width, col.y + col.height, col.x, col.y + col.height);
		if (line.intersects(rect)) return new float[]{0, -1};

		line.setLine(col.x, col.y + col.height, col.x, col.y);
		if (line.intersects(rect)) return new float[]{1, 0};

		return new float[2];
	}
}
