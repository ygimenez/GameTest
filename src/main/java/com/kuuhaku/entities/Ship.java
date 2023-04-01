package com.kuuhaku.entities;

import com.kuuhaku.GameRuntime;
import com.kuuhaku.Utils;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.projectiles.ShipBullet;
import com.kuuhaku.interfaces.IDynamic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

public class Ship extends Entity implements IDynamic {
	private final GameRuntime parent;
	private long lastShot;
	private double fireRate = 3;
	private int bullets = 1;
	private double speed = 1;

	public Ship(GameRuntime parent) {
		super("ship", 200);
		this.parent = parent;

		getBounds().setPosition(parent.getSafeArea().width / 2d - getWidth() / 2d, parent.getSafeArea().height - 100);
	}

	@Override
	public GameRuntime getParent() {
		return parent;
	}

	public void move(double dx, double dy) {
		if (!Utils.between(getX() + dx, 0, parent.getSafeArea().width - getWidth())) {
			dx = 0;
		}

		if (!Utils.between(getY() + dy, 0, parent.getSafeArea().height - getHeight())) {
			dy = 0;
		}

		getBounds().translate(dx, dy);
	}

	public double getFireRate() {
		return fireRate;
	}

	public void setFireRate(double fireRate) {
		this.fireRate = fireRate;
	}

	public int getBullets() {
		return bullets;
	}

	public void setBullets(int bullets) {
		this.bullets = bullets;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public void update() {
		move(
				(-parent.keyValue(VK_LEFT) + parent.keyValue(VK_RIGHT)) * speed,
				(-parent.keyValue(VK_UP) + parent.keyValue(VK_DOWN)) * speed
		);

		if (parent.keyState(VK_SPACE)) {
			if (System.currentTimeMillis() - lastShot > 1000 / fireRate) {
				playCue("ship_fire");
				for (int i = 0; i < bullets; i++) {
					double step = 45d / (bullets + 1);
					parent.spawn(new ShipBullet(this, 2, -45 / 2d + step * (i + 1)));
				}

				lastShot = System.currentTimeMillis();
			}
		}
	}

	@Override
	public void destroy() {
		playCue("explode");

		CompletableFuture.runAsync(() -> {
			playCue("game_over");
			parent.close();
		}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
	}
}
