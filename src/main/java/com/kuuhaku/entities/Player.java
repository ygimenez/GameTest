package com.kuuhaku.entities;

import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.decoration.PlayerTrail;
import com.kuuhaku.entities.decoration.Thruster;
import com.kuuhaku.entities.projectiles.PlayerProjectile;
import com.kuuhaku.entities.projectiles.PlayerTorpedo;
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
	private final Cooldown cooldown;
	private int hp, baseHp;
	private float fireRate = 3;
	private int bullets = 2;
	private int damage = 50;
	private float speed = 1;
	private int grace = 0;
	private int bombs = 1;

	public Player(GameRuntime runtime) {
		super(runtime, null);
		this.cooldown = new Cooldown(runtime, (int) (1000 / fireRate));
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
		if (getRuntime().isTraining()) return 1;
		return hp;
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
		this.baseHp = Math.max(this.hp, this.baseHp);
	}

	@Override
	public void damage(int value) {
		if (value < 0 || grace == 0) {
			this.hp = Utils.clamp(hp - value, 0, baseHp);
		}

		if (value > 0 && grace == 0) {
			grace = getRuntime().millisToTick(1000);
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
				getRuntime().keyValue(VK_A) - getRuntime().keyValue(VK_D),
				getRuntime().keyValue(VK_W) - getRuntime().keyValue(VK_S)
		);

		getCoordinates().translate(velocity[0], velocity[1]);

		cooldown.setTime(getRuntime().millisToTick((long) (1000 / fireRate)));
		if (getRuntime().keyState(VK_SPACE)) {
			if (cooldown.use()) {
				AssetManager.playCue("ship_fire");
				for (int i = 0; i < bullets; i++) {
					float step = 30f / (bullets + 1);
					float angle = -30f / 2 + step * (i + 1);

					getRuntime().spawn(new PlayerProjectile(this, bullets > 1 ? Utils.rng().nextFloat(-step, step) : angle));
					Utils.sleep(100);
				}
			}
		} else if (getRuntime().keyState(VK_CONTROL) && bombs > 0) {
			if (cooldown.use()) {
				AssetManager.playCue("ship_fire");
				getRuntime().spawn(new PlayerTorpedo(this));
				bombs--;
			}
		}

		getRuntime().spawn(
				new PlayerTrail(this, 0, getHeight() / 3),
				new PlayerTrail(this, getWidth(), getHeight() / 3)
		);
	}

	public void accelerate(int dx, int dy) {
		float vx = velocity[0];
		float vy = velocity[1];
		float friction = 0.4f;

		float sway = -Utils.clamp(vx - ((vx + speed * dx) * friction) / getRuntime().getFPS(), -1, 1);
		getCoordinates().setAngle((float) Math.toRadians(180 - 30 * sway));

		for (Entity child : getChildren()) {
			if (child instanceof Thruster t) {
				t.setAccel(dy);
			}
		}

		float[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();
		if (!Utils.between(pos[0] + vx, safe.x + getWidth() / 2f, safe.x + safe.width - getWidth() / 2f)) {
			velocity[0] *= -1;
		} else {
			velocity[0] -= ((vx + speed * dx) * friction) / getRuntime().getFPS();
		}

		if (!Utils.between(pos[1] + vy, safe.y + getHeight() / 2f, safe.y + safe.height - getHeight() / 2f)) {
			velocity[1] *= -1;
		} else {
			velocity[1] -= ((vy + speed * dy) * friction) / getRuntime().getFPS();
		}
	}

	@Override
	public void onDestroy() {
		AssetManager.playCue("explode");

		CompletableFuture.runAsync(() -> {
			AssetManager.playCue("game_over");
			getRuntime().close();
		}, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
	}

	public void removeGrace() {
		this.grace = 0;
	}

	public int getBombs() {
		return bombs;
	}

	public void addBomb() {
		this.bombs++;
	}
}
