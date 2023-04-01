package com.kuuhaku.entities.base;

import com.kuuhaku.GameRuntime;
import com.kuuhaku.entities.Ship;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IProjectile;

public abstract class Bullet extends Entity implements IDynamic, IProjectile {
	private final Entity owner;
	private final double speed;
	private final double[] vector;

	public Bullet(Entity owner, String sprite, double speed, double angle) {
		super(sprite, 1);
		this.owner = owner;
		this.speed = speed;

		getBounds().setAngle(owner.getBounds().getAngle() + Math.toRadians(angle));
		vector = new double[]{
				Math.sin(getBounds().getAngle()),
				-Math.cos(getBounds().getAngle())
		};

		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d + owner.getWidth() * vector[0],
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d + owner.getHeight() * vector[1]
		);
	}

	@Override
	public GameRuntime getParent() {
		return owner.getParent();
	}

	@Override
	public void update() {
		getBounds().translate(vector[0] * speed, vector[1] * speed);

		for (Entity entity : getParent().getReadOnlyEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				playCue("hit");
				entity.setHp(entity.getHp() - 50);
				setHp(0);
				break;
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (!getBounds().intersect(getParent().getSafeArea())) return false;

		return (other instanceof Ship == !(owner instanceof Ship)) && getBounds().intersect(other.getBounds());
	}
}
