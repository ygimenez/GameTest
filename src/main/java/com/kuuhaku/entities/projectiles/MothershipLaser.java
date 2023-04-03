package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class MothershipLaser extends Bullet {
	public MothershipLaser(Entity owner, double angle) {
		super(owner, "mothership_laser", 1, 3, angle);
	}
}
