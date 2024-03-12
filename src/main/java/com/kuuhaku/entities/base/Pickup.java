package com.kuuhaku.entities.base;

import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

public abstract class Pickup extends Entity implements IDynamic, ICollide {
	public Pickup(Entity source) {
		super(source.getRuntime(), null);

		Point2D.Float pos = source.getGlobalCenter();
		getCoordinates().setPosition(
				pos.x - getWidth() / 2f + Utils.rng().nextFloat(source.getWidth()),
				pos.y - getHeight() / 2f + Utils.rng().nextFloat(source.getHeight())
		);
	}

	@Override
	public void update() {
		getCoordinates().translate(0, 0.2f);

		for (Entity entity : getRuntime().getEntities()) {
			if (!(entity instanceof Player s)) continue;

			if (hit(entity)) {
				AssetManager.playCue("pickup");
				addBonus(s);
				dispose();
				break;
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}

	public abstract void addBonus(Player player);
}
