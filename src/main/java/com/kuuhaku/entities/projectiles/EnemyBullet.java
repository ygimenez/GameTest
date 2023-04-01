package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class EnemyBullet extends Bullet {
	public EnemyBullet(Entity owner, double speed, double angle) {
		super(owner, "enemy_bullet", speed, angle);
	}
}
