package com.kuuhaku.entities.base;

import com.kuuhaku.entities.Player;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

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

		getCoordinates().setAngle((float) (source.getAngle() + Math.toRadians(angle)));
		int radius = Math.max(source.getWidth(), source.getHeight()) / 2;
		Point2D.Float center = source.getGlobalCenter();
		getCoordinates().setPosition(
				center.x - getWidth() / 2f - Utils.fsin(getAngle()) * radius,
				center.y - getHeight() / 2f + Utils.fcos(getAngle()) * getHeight() / 2f + Utils.fcos(getAngle()) * radius
		);
	}

	@Override
	public void update() {
		float[] vector = Utils.angToVec(getAngle());
		getCoordinates().translate(vector[0] * speed, vector[1] * speed);

		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof Projectile) continue;

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
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;

		return other instanceof Player != source instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}

	protected float getSpeed() {
		return speed;
	}

	protected void setSpeed(float speed) {
		this.speed = speed;
	}

	public Entity getSource() {
		return source;
	}
}
