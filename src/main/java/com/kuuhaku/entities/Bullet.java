package com.kuuhaku.entities;

import com.kuuhaku.Game;

public class Bullet extends Entity implements IDynamic, IProjectile {
	private final Entity owner;
	private final double speed;
	private final double[] vector;

	public Bullet(Entity owner, double speed, double angle) {
		super(owner instanceof Ship ? "bullet.png" : "enemy_bullet.png", 1);
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
	public Game getParent() {
		return owner.getParent();
	}

	@Override
	public void update() {
		getBounds().translate(vector[0] * speed, vector[1] * speed);

		for (Entity entity : getParent().getReadOnlyEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				entity.setHp(entity.getHp() - 50);

				setHp(0);
				break;
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		return !(owner.getClass().equals(other.getClass())) && getBounds().intersect(other.getBounds());
	}
}
