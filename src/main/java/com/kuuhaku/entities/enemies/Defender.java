package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

public class Defender extends Enemy {
	private final Mothership owner;
	private int angle, radius;

	public Defender(Mothership parent) {
		super(parent.getRuntime(), "snake", 250, 10, 1);
		this.owner = parent;

		double[] pos = parent.getPosition();
		getBounds().setPosition(
				pos[0] + parent.getWidth() / 2d - getWidth() / 2d,
				pos[1] + parent.getHeight() / 2d - getHeight() / 2d
		);
	}

	@Override
	public void attack() {
		if (owner.isEnraged() && getCooldown().use() && Utils.rng().nextDouble() > 0.8) {
			AssetManager.playCue("enemy_fire");
			getRuntime().spawn(new EnemyBullet(this, 1, Utils.angBetween(this, getRuntime().getRandomPlayer())));
		}
	}

	@Override
	public void move() {
		double[] pos = owner.getPosition();
 		getBounds().setPosition(
				pos[0] + owner.getWidth() / 2d - getWidth() / 2d + Utils.fsin(Math.toRadians(angle)) * radius / 2d,
				pos[1] + owner.getHeight() / 2d - getHeight() / 2d + Utils.fcos(Math.toRadians(angle)) * radius / 2d
		);

		if (radius < 180) {
			radius++;
		} else {
			angle++;
		}
	}
}
