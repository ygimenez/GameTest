package com.kuuhaku.entities.base;

import com.kuuhaku.AssetManager;
import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.entities.pickups.FastshotPickup;
import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.entities.pickups.MultishotPickup;
import com.kuuhaku.entities.pickups.SpeedPickup;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.interfaces.IDynamic;

import java.util.List;

public abstract class Enemy extends Entity implements IDynamic {
	private final GameRuntime parent;
	private final int fireRate;
	private final int bullets;
	private final int points;
	private long lastShot;

	public Enemy(GameRuntime parent, String sprite, int hp, int fireRate, int bullets) {
		super(sprite, hp);
		this.parent = parent;
		this.fireRate = fireRate;
		this.bullets = bullets;
		this.points = getHp() / 3 + 20 * fireRate + 100 * bullets;

		getBounds().setPosition(Math.random() * (getParent().getSafeArea().width - getWidth()), getParent().getBounds().y);
	}

	@Override
	public GameRuntime getParent() {
		return parent;
	}

	public int getPoints() {
		return points;
	}

	public boolean isBoss() {
		return points > 1000;
	}

	@Override
	public void setHp(int hp) {
		super.setHp(hp);
		if (hp == 0) {
			AssetManager.playCue("explode");
			parent.addScore(points);

			if (Math.random() > 0.8) {
				parent.spawn(List.of(
						new MultishotPickup(this),
						new FastshotPickup(this),
						new SpeedPickup(this),
						new HealthPickup(this)
				).get((int) (Math.random() * 4)));
			}
		}
	}

	public abstract void move();

	@Override
	public void destroy() {
		getParent().getSpawnLimit().release();
	}

	@Override
	public void update() {
		move();

		if (getBounds().intersect(getParent().getSafeArea())) {
			if (System.currentTimeMillis() - lastShot > 5000 / fireRate) {
				for (int i = 0; i < bullets; i++) {
					double step = 45d / (bullets + 1);
					parent.spawn(new EnemyBullet(this, 1, 180 + -45 / 2d + step * (i + 1)));
				}

				lastShot = System.currentTimeMillis();
			}
		}
	}
}
