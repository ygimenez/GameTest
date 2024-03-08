package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.Metadata;

@Metadata(sprite = "enemy_bullet")
public class EnemyProjectile extends Projectile {
	public EnemyProjectile(Enemy source, float speed, float angle) {
		super(source, (int) (25 * source.getDamageMult()), speed * source.getSpeedMult(), angle);
	}
}
