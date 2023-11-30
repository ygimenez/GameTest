package com.kuuhaku.entities.base;

import com.kuuhaku.entities.Player;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

public abstract class Bullet extends Entity implements IDynamic, IProjectile {
	private final Entity source;
	private final int damage;
	private double speed;

	public Bullet(Entity source, String sprite, int damage, double speed, double angle) {
		this(source, new Sprite(source.getRuntime(), sprite), damage, speed, angle);
	}

	public Bullet(Entity source, Sprite sprite, int damage, double speed, double angle) {
		super(source.getRuntime(), null, sprite, 1);
		this.source = source;
		this.speed = speed;
		this.damage = damage;

		getBounds().setAngle(source.getAngle() + Math.toRadians(angle));
		int radius = Math.max(source.getWidth(), source.getHeight()) / 2;
		Point2D center = source.getCenter();
		getBounds().setPosition(
				center.getX() - getWidth() / 2d - Utils.fsin(getAngle()) * radius,
				center.getY() - getHeight() / 2d + Utils.fcos(getAngle()) * getHeight() / 2d + Utils.fcos(getAngle()) * radius
		);
	}

	@Override
	public void update() {
		double[] vector = Utils.angToVec(getAngle());
		getBounds().translate(vector[0] * speed, vector[1] * speed);

		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				AssetManager.playCue("hit");
				entity.setHp(entity.getHp() - damage);
				setHp(0);
				break;
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (!other.getBounds().intersect(getRuntime().getSafeArea())) return false;

		return other instanceof Player != source instanceof Player && getBounds().intersect(other.getBounds());
	}

	protected double getSpeed() {
		return speed;
	}

	protected void setSpeed(double speed) {
		this.speed = speed;
	}

	public Entity getSource() {
		return source;
	}
}
