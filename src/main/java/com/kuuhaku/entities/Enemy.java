package com.kuuhaku.entities;

import com.kuuhaku.Game;

import java.util.List;

public abstract class Enemy extends Entity implements IDynamic {
	private final Game parent;
	private final int fireRate;
	private final int bullets;
	private long lastShot;

	public Enemy(Game parent, String filename, int hp, int fireRate, int bullets) {
		super(filename, hp);
		this.parent = parent;
		this.fireRate = fireRate;
		this.bullets = bullets;

		getBounds().setPosition(Math.random() * (getParent().getSafeArea().width - getWidth()), getParent().getBounds().y);
	}

	@Override
	public Game getParent() {
		return parent;
	}

	@Override
	public void setHp(int hp) {
		super.setHp(hp);
		if (hp == 0) {
			parent.addScore(getHp() / 3 + 20 * fireRate + 100 * bullets);

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
	public void update() {
		move();

		if (getBounds().intersect(getParent().getSafeArea())) {
			if (System.currentTimeMillis() - lastShot > 5000 / fireRate) {
				for (int i = 0; i < bullets; i++) {
					double step = 45d / (bullets + 1);
					parent.spawn(new Bullet(this, 1, 180 + -45 / 2d + step * (i + 1)));
				}

				lastShot = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void destroy() {
		parent.getEnemies().release();
	}
}
