package com.kuuhaku.entities.base;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.interfaces.IDynamic;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.interfaces.ITrackable;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.view.GameRuntime;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Enemy extends Entity implements IDynamic, ITrackable {
	private static final List<Class<Pickup>> drops = new ArrayList<>();

	private final int fireRate;
	private final int bullets;
	private final int points;
	private final Cooldown cooldown;

	static {
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.pickups")) {
			drops.add((Class<Pickup>) klass);
		}
	}

	public Enemy(GameRuntime runtime, String sprite, int hp, int fireRate, int bullets) {
		this(runtime, new Sprite(runtime, sprite), hp, fireRate, bullets);
	}

	public Enemy(GameRuntime runtime, Sprite sprite, int hp, int fireRate, int bullets) {
		super(runtime, null, sprite, hp + (int) (runtime.getTick() / 1000));
		this.fireRate = fireRate;
		this.bullets = bullets;
		this.points = getHp() / 3 + 20 * fireRate + 100 * bullets;
		this.cooldown = new Cooldown(runtime, 5000 / fireRate);

		getBounds().setPosition(
				Utils.rng().nextDouble(getRuntime().getSafeArea().width * 0.8 - getWidth()),
				getRuntime().getBounds().y + 50
		);
	}

	public int getPoints() {
		return points;
	}

	protected int getFireRate() {
		return fireRate;
	}

	protected int getBullets() {
		return bullets;
	}

	protected Cooldown getCooldown() {
		return cooldown;
	}

	@Override
	public void setHp(int hp) {
		super.setHp(hp);
		if (hp <= 0) {
			AssetManager.playCue("explode");
			getRuntime().addScore(points);

			if (Utils.rng().nextDouble() > 1 - (points * 0.0001)) {
				Class<Pickup> drop = drops.get(ThreadLocalRandom.current().nextInt(drops.size()));

				try {
					getRuntime().spawn(drop.getConstructor(Entity.class).newInstance(this));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract void move();

	@Override
	public void onDestroy() {
		getRuntime().getSpawnLimit().release();
		getRuntime().releaseDifficulty(getPoints());
	}

	@Override
	public void update() {
		move();

		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				int eHp = entity.getHp();
				entity.setHp(entity.getHp() - getHp());
				setHp(getHp() - eHp);
				break;
			}
		}

		if (getBounds().intersect(getRuntime().getSafeArea())) {
			attack();
		}
	}

	public void attack() {
		if (bullets > 0 && cooldown.use()) {
			AssetManager.playCue("enemy_fire");
			for (int i = 0; i < bullets; i++) {
				double step = 45d / (bullets + 1);
				getRuntime().spawn(new EnemyBullet(this, 1, -45 / 2d + step * (i + 1)));
			}
		}
	}

	public boolean hit(Entity other) {
		if (!getBounds().intersect(getRuntime().getSafeArea())) return false;

		return other instanceof Player && getBounds().intersect(other.getBounds());
	}
}
