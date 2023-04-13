package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.manager.AssetManager;

import java.util.concurrent.ThreadLocalRandom;

public class Defender extends Enemy {
	private final Mothership owner;
	private int angle, radius;

	public Defender(Mothership owner) {
		super(owner.getParent(), "snake", 250, 10, 1);
		this.owner = owner;

		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d
		);
	}

	@Override
	public void update() {
		move();

		for (Entity entity : getParent().getEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				int eHp = entity.getHp();
				entity.setHp(entity.getHp() - getHp());
				setHp(getHp() - eHp);
				break;
			}
		}

		if (getBounds().intersect(getParent().getSafeArea())) {
			if (owner.isEnraged() && getCooldown().use() && ThreadLocalRandom.current().nextDouble() > 0.8) {
				Entity player = getParent().getPlayer();
				double dx = (player.getX() + player.getWidth() / 2d) - (getX() + getWidth() / 2d);
				double dy = (player.getY() + player.getHeight() / 2d) - (getY() + getHeight() / 2d);

				AssetManager.playCue("enemy_fire");
				getParent().spawn(new EnemyBullet(this, 1, 90 + Math.toDegrees(Math.atan2(dy, dx))));
			}
		}
	}

	@Override
	public void move() {
		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d + Math.sin(Math.toRadians(angle)) * radius / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d + Math.cos(Math.toRadians(angle)) * radius / 2d
		);

		if (radius < 180) {
			radius++;
		} else {
			angle++;
		}
	}
}
