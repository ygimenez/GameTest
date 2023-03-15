package com.kuuhaku.entities;

import com.kuuhaku.Game;

public abstract class Pickup extends Entity implements IDynamic, IProjectile {
	private final Entity owner;

	public Pickup(Entity owner, String filename) {
		super(filename, 1);
		this.owner = owner;

		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d
		);
	}

	@Override
	public Game getParent() {
		return owner.getParent();
	}

	@Override
	public void update() {
		getBounds().translate(0, 0.2);

		for (Entity entity : getParent().getReadOnlyEntities()) {
			if (!(entity instanceof Ship s)) continue;

			if (hit(entity)) {
				addBonus(s);
				setHp(0);
				break;
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		return other instanceof Ship && getBounds().intersect(other.getBounds());
	}

	public abstract void addBonus(Ship ship);
}
