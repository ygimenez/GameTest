package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.enemies.Destroyer;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

public class DestroyerTorpedo extends Bullet {
	public DestroyerTorpedo(Entity owner, double speed) {
		super(owner, "destroyer_torpedo", 100, speed, 180);
	}

	@Override
	public void update() {
		if (getOwner() instanceof Destroyer d && d.getHp() == 0) {
			setHp(0);
			return;
		}

		Entity player = getParent().getPlayer();

		getBounds().setAngle(Math.toRadians(90 + Utils.vecToAng(player.getCenter(), getCenter())));
		getBounds().translate(
				Math.sin(getBounds().getAngle()) * getSpeed(),
				-Math.cos(getBounds().getAngle()) * getSpeed()
		);

		for (Entity entity : getParent().getEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				AssetManager.playCue("explode");
				entity.setHp(entity.getHp() - getDamage());
				setHp(0);
				break;
			}
		}
	}

	@Override
	public void destroy() {
		if (getOwner() instanceof Destroyer d) {
			d.setTorpLimit(d.getTorpLimit() + 1);
		}
	}
}
