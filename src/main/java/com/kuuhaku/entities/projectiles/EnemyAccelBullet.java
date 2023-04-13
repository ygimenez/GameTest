package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class EnemyAccelBullet extends Bullet {
	public EnemyAccelBullet(Entity owner, double initialSpeed, double angle) {
		super(owner, "enemy_bullet", 50, initialSpeed, angle);
	}

	@Override
	public void update() {
		setSpeed(getSpeed() + 0.0025);
		super.update();
	}
}
