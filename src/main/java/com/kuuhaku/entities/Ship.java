package com.kuuhaku.entities;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Cooldown;
import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.Utils;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.projectiles.ShipBullet;
import com.kuuhaku.interfaces.IDynamic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

public class Ship extends Entity implements IDynamic {
	private final GameRuntime parent;
	private final Cooldown cooldown;
	private double fireRate = 3;
	private int bullets = 1;
	private int damage = 50;
	private double speed = 1;

	public Ship(GameRuntime parent) {
		super("ship", 200);
		this.parent = parent;
		this.cooldown = new Cooldown(parent, (int) (500 / fireRate));

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

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public void update() {
		move(
				(-parent.keyValue(VK_A) + parent.keyValue(VK_D)) * speed,
				(-parent.keyValue(VK_W) + parent.keyValue(VK_S)) * speed
		);

		if (parent.keyState(VK_SPACE)) {
			cooldown.setTime((int) (500 / fireRate));
			if (cooldown.use()) {
				AssetManager.playCue("ship_fire");
				for (int i = 0; i < bullets; i++) {
					double step = 30d / (bullets + 1);
					parent.spawn(new ShipBullet(this, damage * 2 / (bullets + 1), fireRate, -30 / 2d + step * (i + 1)));
				}
			}
		}
	}

	@Override
	public void destroy() {
		AssetManager.playCue("explode");

		CompletableFuture.runAsync(() -> {
			AssetManager.playCue("game_over");
			parent.close();
		}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
	}
}
