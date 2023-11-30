package com.kuuhaku.entities.other;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.base.Sprite;
import com.kuuhaku.entities.enemies.Mothership;
import com.kuuhaku.interfaces.ICollide;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IParticle;

public class MothershipLaser extends Entity implements IDynamic, ICollide {
	private float angle = 450;

	public MothershipLaser(Mothership owner) {
		super(owner.getRuntime(), owner, new Sprite(owner.getRuntime(), "laser", 4, 1, 20, true));
		getCoordinates().setPosition(owner.getWidth() / 2f - getWidth() / 2f, owner.getHeight());
		getCoordinates().setAnchor(0.5f, 0);
	}

	@Override
	public void update() {
		getCoordinates().setAngle((float) Math.toRadians(angle / 10));

		if (--angle <= -450) super.setHp(0);
		else {
			for (Entity entity : getRuntime().getEntities()) {
				if (!(entity instanceof Player)) continue;

				if (hit(entity)) {
					entity.setHp(entity.getHp() - 2);
					((Player) entity).removeGrace();
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

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}
}
