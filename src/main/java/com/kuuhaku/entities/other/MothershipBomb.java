package com.kuuhaku.entities.other;

import com.kuuhaku.entities.base.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;
import com.kuuhaku.utils.Utils;

import java.awt.geom.Point2D;

public class MothershipBomb extends Entity implements IDynamic, ICollide {
	private final Mothership owner;

	public MothershipBomb(Mothership owner) {
		super(owner.getRuntime(), null, new Sprite(owner.getRuntime(), "artillery", 8, 2,
				owner.isEnraged() ? 15 : 20, 0, false
		));
		this.owner = owner;

		float rx = (30 + Utils.rng().nextFloat(100)) * (Utils.rng().nextBoolean() ? -1 : 1);
		float ry = (30 + Utils.rng().nextFloat(100)) * (Utils.rng().nextBoolean() ? -1 : 1);

		Point2D.Float player = getRuntime().getRandomPlayer().getGlobalCenter();
		getCoordinates().setPosition(player.x + rx, player.y + ry);
	}

	@Override
	public void update() {
		if (getSprite().hasFinished()) dispose();
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					d.damage((int) (100 * owner.getDamageMult()));
					break;
				}
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;
		else if (!Utils.between(getSprite().getFrame(), 8, 11)) return false;

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
