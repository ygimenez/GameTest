package com.kuuhaku.entities.other;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;

public class MothershipLaserShot extends Entity implements IDynamic, ICollide {
	private final Mothership owner;

	public MothershipLaserShot(Mothership owner) {
		this(owner, 0, 0, 0);
	}

	public MothershipLaserShot(Mothership owner, int x, int y, float angle) {
		super(owner.getRuntime(), null, new Sprite(owner.getRuntime(), "laser_shot", 13, 1, 20, false));
		this.owner = owner;

		getCoordinates().setPosition(x, y);
		getCoordinates().setAngle((float) Math.toRadians(angle));
	}

	@Override
	public void update() {
		if (getSprite().getFrame() == 12) dispose();
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					d.damage((int) (75 * owner.getDamageMult()));
					break;
				}
			}
		}
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;
		else if (getSprite().getFrame() != 10) return false;

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
