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

public class MothershipLaserSweep extends Entity implements IDynamic, ICollide {
	private final Mothership owner;
	private float angle = 450;
	boolean reverse = Utils.rng().nextBoolean();

	public MothershipLaserSweep(Mothership owner) {
		super(owner.getRuntime(), owner, new Sprite(owner.getRuntime(), "laser_sweep", 4, 1, 20, 0, true));
		this.owner = owner;

		getCoordinates().setPosition(owner.getWidth() / 2f, owner.getHeight());
		getCoordinates().setAnchor(0.5f, 0);
	}

	@Override
	public void update() {
		getCoordinates().setAngle((float) Math.toRadians((reverse ? -angle : angle) / 10));

		if (--angle <= -450) dispose();
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (entity instanceof IDamageable d && hit(entity)) {
					d.damage((int) (2 * owner.getDamageMult()));
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
