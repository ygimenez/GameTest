package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.interfaces.Metadata;

@Metadata(sprite = "bullet")
public class PlayerProjectile extends Projectile {
	public PlayerProjectile(Entity source, int damage, float speed, float angle) {
		super(source, damage, speed, angle);
	}
}
