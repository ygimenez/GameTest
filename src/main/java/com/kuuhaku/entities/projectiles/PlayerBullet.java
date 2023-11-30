package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Bullet;
import com.kuuhaku.entities.base.Entity;

public class PlayerBullet extends Bullet {
	public PlayerBullet(Entity source, int damage, double speed, double angle) {
		super(source, "bullet", damage, speed, angle);
	}
}
