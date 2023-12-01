package com.kuuhaku.entities.projectiles;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Projectile;
import com.kuuhaku.interfaces.Metadata;

@Metadata(sprite = "bullet")
public class PlayerProjectile extends Projectile {
	public PlayerProjectile(Player source, float angle) {
		super(source, source.getDamage() * 2 / (source.getBullets() + 1), source.getFireRate(), angle);
	}
}
