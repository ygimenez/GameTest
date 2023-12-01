package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.projectiles.EnemyProjectile;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;

@Metadata(sprite = "snake", hp = 500)
public class Defender extends Enemy {
	private final Mothership owner;
	private int angle, radius;

	public Defender(Mothership parent) {
		super(parent.getRuntime(), parent, 1000);
		this.owner = parent;

		getCoordinates().setPosition(parent.getWidth() / 2f, parent.getHeight() / 2f);
	}

	@Override
	public void move() {
		getCoordinates().setPosition(
				getParent().getWidth() / 2f + Utils.fsin((float) Math.toRadians(angle)) * radius / 2f,
				getParent().getHeight() / 2f + Utils.fcos((float) Math.toRadians(angle)) * radius / 2f
		);

		if (radius < 180) {
			radius++;
		}

		angle++;
	}

	@Override
	public void damage(int value) {
		if (radius < 180) return;
		super.damage(value);
		owner.damage(value / 10);
	}

	@Override
	public void shoot() {
		if (owner.isEnraged() && Utils.rng().nextFloat() > 0.5f) {
			AssetManager.playCue("enemy_fire");
			getRuntime().spawn(new EnemyProjectile(this, 1, Utils.angBetween(this, getRuntime().getRandomPlayer())));
		}
	}
}
