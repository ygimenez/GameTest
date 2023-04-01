package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class ShipBullet extends Bullet {
	public ShipBullet(Entity owner, double speed, double angle) {
		super(owner, "bullet", speed, angle);
	}
}
