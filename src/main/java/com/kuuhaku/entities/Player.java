package com.kuuhaku.entities;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.decoration.PlayerTrail;
import com.kuuhaku.entities.decoration.Thruster;
import com.kuuhaku.entities.projectiles.PlayerProjectile;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.awt.event.KeyEvent.*;

@Metadata(sprite = "ship", hp = 200)
public class Player extends Entity implements IDynamic, IDamageable {
	private final float[] velocity = {0, 0};
	private final GameRuntime runtime;
	private final Cooldown cooldown;
	private int hp, baseHp;
	private float fireRate = 3;
	private int bullets = 1;
	private int damage = 50;
	private float speed = 1;
	private int grace = 0;

	public Player(GameRuntime runtime) {
		super(runtime, null);
		this.runtime = runtime;
		this.cooldown = new Cooldown(runtime, (int) (500 / fireRate));
		this.hp = this.baseHp = 200;

		getCoordinates().setPosition(runtime.getSafeArea().width / 2f, runtime.getSafeArea().height - 100);
		runtime.spawn(new Thruster(this));
	}

	@Override
	public int getBaseHp() {
		return baseHp;
	}

	@Override
	public void setBaseHp(int hp) {
		this.baseHp = hp;
	}

	@Override
	public int getHp() {
		if (runtime.isTraining()) return 1;
		return hp;
	}

	@Override
	public void damage(int value) {
		if (value > 0 || grace == 0) {
			this.hp = Utils.clamp(hp - value, 0, baseHp);
		}

		if (value < 0 && grace == 0) {
			grace = runtime.millisToTick(1000);
		}
	}

	public float getFireRate() {
		return fireRate;
	}

	public void setFireRate(float fireRate) {
		this.fireRate = fireRate;
	}

	public int getBullets() {
		return bullets;
	}

	public void setBullets(int bullets) {
		this.bullets = bullets;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
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

		getCoordinates().translate(velocity[0], velocity[1]);

		if (runtime.keyState(VK_SPACE)) {
			cooldown.setTime(runtime.millisToTick((long) (2000 / fireRate)));
			if (cooldown.use()) {
				AssetManager.playCue("ship_fire");
				for (int i = 0; i < bullets; i++) {
					float step = 30f / (bullets + 1);
					runtime.spawn(new PlayerProjectile(this, damage * 2 / (bullets + 1), fireRate, -30f / 2 + step * (i + 1)));
				}
			}
		}

		runtime.spawn(
				new PlayerTrail(this, 0, getHeight() / 3),
				new PlayerTrail(this, getWidth(), getHeight() / 3)
		);
	}

	public void accelerate(int dx, int dy) {
		float vx = velocity[0];
		float vy = velocity[1];
		float friction = 0.4f;

		float sway = -Utils.clamp(vx - ((vx + speed * dx) * friction) / runtime.getFPS(), -1, 1);
		getCoordinates().setAngle((float) Math.toRadians(180 - 30 * sway));

		for (Entity child : getChildren()) {
			if (child instanceof Thruster t) {
				t.setAccel(dy);
			}
		}

		float[] pos = getPosition();
		Rectangle safe = runtime.getSafeArea();
		if (!Utils.between(pos[0] + vx, safe.x, safe.x + safe.width - getWidth())) {
			velocity[0] *= -1;
		} else {
			velocity[0] -= ((vx + speed * dx) * friction) / runtime.getFPS();
		}

		if (!Utils.between(pos[1] + vy, safe.y, safe.y + safe.height - getHeight())) {
			velocity[1] *= -1;
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

	public void removeGrace() {
		this.grace = 0;
	}
}
