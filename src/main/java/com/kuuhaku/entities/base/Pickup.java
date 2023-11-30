package com.kuuhaku.entities.base;

import com.kuuhaku.entities.Player;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

public abstract class Pickup extends Entity implements IDynamic, IProjectile {
	public Pickup(Entity source, String sprite) {
		super(source.getRuntime(), null, sprite, 1);

		double[] pos = source.getPosition();
		getBounds().setPosition(
				pos[0] + source.getWidth() / 2d - getWidth() / 2d + Utils.rng().nextDouble(10) - 5,
				pos[1] + source.getHeight() / 2d - getHeight() / 2d + Utils.rng().nextDouble(10) - 5
		);
	}

	@Override
	public void update() {
		getBounds().translate(0, 0.2);

		for (Entity entity : getRuntime().getEntities()) {
			if (!(entity instanceof Player s)) continue;

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
		return other instanceof Player && getBounds().intersect(other.getBounds());
	}

	public abstract void addBonus(Player player);
}
