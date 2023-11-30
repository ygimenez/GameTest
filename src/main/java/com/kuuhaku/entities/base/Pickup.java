package com.kuuhaku.entities.base;

import com.kuuhaku.entities.Player;
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
				pos.x + source.getWidth() / 2f - getWidth() / 2f + Utils.rng().nextFloat(10) - 5,
				pos.y + source.getHeight() / 2f - getHeight() / 2f + Utils.rng().nextFloat(10) - 5
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
				setHp(0);
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
