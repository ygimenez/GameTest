package com.kuuhaku.entities;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.decoration.PlayerTrail;
import com.kuuhaku.entities.decoration.Thruster;
import com.kuuhaku.entities.projectiles.PlayerBullet;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

public class Player extends Entity implements IDynamic {
	private final double[] velocity = {0, 0};
	private final GameRuntime runtime;
	private final Cooldown cooldown;
	private double fireRate = 3;
	private int bullets = 2;
	private int damage = 50;
	private double speed = 1;
	private int grace = 0;

	public Player(GameRuntime runtime) {
		super(runtime, null, "ship", 200);
		this.runtime = runtime;
		this.cooldown = new Cooldown(runtime, (int) (500 / fireRate));

		getBounds().setPosition(runtime.getSafeArea().width / 2d - getWidth() / 2d, runtime.getSafeArea().height - 100);
		runtime.spawn(new Thruster(this));
	}

	@Override
	public int getHp() {
		if (runtime.isTraining()) return 1;

		return super.getHp();
	}

	@Override
	public void setHp(int hp) {
		int before = getHp();
		if (hp >= before || grace == 0) {
			super.setHp(hp);
		}

		if (hp < before && grace == 0) {
			grace = runtime.millisToTick(1000);
		}
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
	public BufferedImage getImage() {
		if (grace > 0 && Utils.between(grace % 30, 0, 15)) return null;

		return super.getImage();
	}

	@Override
	public void update() {
		if (grace > 0) grace--;

		accelerate(
				runtime.keyValue(VK_A) - runtime.keyValue(VK_D),
				runtime.keyValue(VK_W) - runtime.keyValue(VK_S)
		);

		getBounds().translate(velocity[0], velocity[1]);

		if (runtime.keyState(VK_SPACE)) {
			cooldown.setTime((int) (500 / fireRate));
			if (cooldown.use()) {
				AssetManager.playCue("ship_fire");
				for (int i = 0; i < bullets; i++) {
					double step = 30d / (bullets + 1);
					runtime.spawn(new PlayerBullet(this, damage * 2 / (bullets + 1), fireRate, -30d / 2 + step * (i + 1)));
				}
			}
		}

		runtime.spawn(
				new PlayerTrail(this, 0, getHeight() / 3),
				new PlayerTrail(this, getWidth(), getHeight() / 3)
		);
	}

	public void accelerate(int dx, int dy) {
		double vx = velocity[0];
		double vy = velocity[1];
		double friction = 0.4;

		double sway = -Utils.clamp(vx - ((vx + speed * dx) * friction) / runtime.getFPS(), -1, 1);
		getBounds().setAngle(Math.toRadians(180 - 30 * sway));

		for (Entity child : getChildren()) {
			if (child instanceof Thruster t) {
				t.setAccel(dy);
			}
		}

		double[] pos = getPosition();
		Rectangle safe = runtime.getSafeArea();
		if (!Utils.between(pos[0] + vx, safe.x, safe.x + safe.width - getWidth())) {
			velocity[0] /= -2;
		} else {
			velocity[0] -= ((vx + speed * dx) * friction) / runtime.getFPS();
		}

		if (!Utils.between(pos[1] + vy, safe.y, safe.y + safe.height - getHeight())) {
			velocity[1] /= -2;
		} else {
			velocity[1] -= ((vy + speed * dy) * friction) / runtime.getFPS();
		}
	}

	@Override
	public void onDestroy() {
		AssetManager.playCue("explode");

		CompletableFuture.runAsync(() -> {
			AssetManager.playCue("game_over");
			runtime.close();
		}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
	}
}
