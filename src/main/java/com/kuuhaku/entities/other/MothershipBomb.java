package com.kuuhaku.entities.other;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.utils.Utils;

import java.awt.*;
import java.awt.geom.Point2D;

public class MothershipBomb extends Entity implements IDynamic, ICollide {
	public MothershipBomb(Mothership owner) {
		super(owner.getRuntime(), null, new Sprite(owner.getRuntime(), "artillery", 8, 2, 20, false));

		float rx = (125 + Utils.rng().nextFloat(50)) * (Utils.rng().nextBoolean() ? -1 : 1);
		float ry = (125 + Utils.rng().nextFloat(50)) * (Utils.rng().nextBoolean() ? -1 : 1);

		Point2D.Float player = getRuntime().getRandomPlayer().getGlobalCenter();
		getCoordinates().setPosition(player.x + rx, player.y + ry);
	}

	@Override
	public void update() {
		if (getSprite().getFrame() == 15) super.setHp(0);
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (!(entity instanceof Player)) continue;

				if (hit(entity)) {
					entity.setHp(entity.getHp() - 100);
					break;
				}
			}
		}
	}

	@Override
	public void setHp(int hp) {
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;
		else if (!Utils.between(getSprite().getFrame(), 8, 13)) return false;

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
