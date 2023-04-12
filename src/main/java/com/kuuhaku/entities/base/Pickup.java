package com.kuuhaku.entities.base;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.entities.Ship;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IProjectile;

public abstract class Pickup extends Entity implements IDynamic, IProjectile {
	private final Entity owner;

	public Pickup(Entity owner, String sprite) {
		super(sprite, 1);
		this.owner = owner;

		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d
		);
	}

	@Override
	public GameRuntime getParent() {
		return owner.getParent();
	}

	@Override
	public void update() {
		getBounds().translate(0, 0.2);

		for (Entity entity : getParent().getReadOnlyEntities()) {
			if (!(entity instanceof Ship s)) continue;

			if (hit(entity)) {
				AssetManager.playCue("pickup");
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
