package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class EnemyBullet extends Bullet {
	public EnemyBullet(Entity source, double speed, double angle) {
		super(source, "enemy_bullet", 50, speed, angle);
	}
}
