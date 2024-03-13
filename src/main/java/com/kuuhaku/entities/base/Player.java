package com.kuuhaku.entities.base;

import com.kuuhaku.entities.decoration.PlayerTrail;
import com.kuuhaku.entities.decoration.Thruster;
import com.kuuhaku.interfaces.IDamageable;
import com.kuuhaku.interfaces.IDescribable;
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

public abstract class Player extends Entity implements IDynamic, IDamageable, IDescribable {
	private final float[] velocity = {0, 0};
	private final Cooldown atkCooldown, spCooldown;
	private int hp, baseHp;
	private float fireRate;
	private int bullets;
	private int damage;
	private float speed;
	private int grace = 0;

	public Player(GameRuntime runtime, float fireRate, float specialRate, int bullets, int damage, float speed) {
		super(runtime, null);
		this.atkCooldown = new Cooldown(runtime, (int) (1000 / fireRate));
		this.spCooldown = new Cooldown(runtime, (int) (5000 / specialRate));

		Metadata info = getClass().getDeclaredAnnotation(Metadata.class);
		this.baseHp = this.hp = info.hp();

		this.fireRate = fireRate;
		this.bullets = bullets;
		this.damage = damage;
		this.speed = speed;

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

		atkCooldown.setTime(getRuntime().millisToTick((long) (1000 / fireRate)));
		if (getRuntime().keyState(VK_SPACE) && atkCooldown.use()) {
			shoot();
		}

		if (getRuntime().keyState(VK_CONTROL) && spCooldown.use()) {
			special();
		}

		getRuntime().spawn(
				new PlayerTrail(this, 0, getHeight() / 3),
				new PlayerTrail(this, getWidth(), getHeight() / 3)
		);
	}

	private void accelerate(int dx, int dy) {
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

	abstract protected void shoot();

	abstract protected void special();

	public Cooldown getAtkCooldown() {
		return atkCooldown;
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

	public Cooldown getSpecial() {
		return spCooldown;
	}
}
