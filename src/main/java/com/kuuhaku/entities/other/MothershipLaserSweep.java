package com.kuuhaku.entities.other;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;

public class MothershipLaserSweep extends Entity implements IDynamic, ICollide {
	private float angle = 450;

	public MothershipLaserSweep(Mothership owner) {
		super(owner.getRuntime(), owner, new Sprite(owner.getRuntime(), "laser_sweep", 4, 1, 20, true));
		getCoordinates().setPosition(owner.getWidth() / 2f, owner.getHeight());
		getCoordinates().setAnchor(0.5f, 0);
	}

	@Override
	public void update() {
		getCoordinates().setAngle((float) Math.toRadians(angle / 10));

		if (--angle <= -450) dispose();
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					d.setHp(d.getHp() - 2);
					((Player) entity).removeGrace();
					break;
				}
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
