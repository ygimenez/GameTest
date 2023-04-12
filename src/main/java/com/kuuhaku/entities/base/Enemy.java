package com.kuuhaku.entities.base;

import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.entities.Ship;
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

	private final GameRuntime parent;
	private final int fireRate;
	private final int bullets;
	private final int points;
	private final Cooldown cooldown;

	static {
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.pickups")) {
			drops.add((Class<Pickup>) klass);
		}
	}

	public Enemy(GameRuntime parent, String sprite, int hp, int fireRate, int bullets) {
		super(sprite, hp + (int) (parent.getTick() / 1000));
		this.parent = parent;
		this.fireRate = fireRate;
		this.bullets = bullets;
		this.points = getHp() / 3 + 20 * fireRate + 100 * bullets;
		this.cooldown = new Cooldown(parent, 2500 / fireRate);

		getBounds().setPosition(
				ThreadLocalRandom.current().nextDouble() * (getParent().getSafeArea().width * 0.8 - getWidth()),
				getParent().getBounds().y + 50
		);
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
			parent.addScore(points);

			if (ThreadLocalRandom.current().nextDouble() > 1 - (points * 0.0001)) {
				Class<Pickup> drop = drops.get(ThreadLocalRandom.current().nextInt(drops.size()));

				try {
					parent.spawn(drop.getConstructor(Entity.class).newInstance(this));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract void move();

	@Override
	public void destroy() {
		getParent().getSpawnLimit().release();
		getParent().releaseDifficulty(getPoints());
	}

	@Override
	public void update() {
		move();

		for (Entity entity : getParent().getReadOnlyEntities()) {
			if (entity instanceof IProjectile) continue;

			if (hit(entity)) {
				int eHp = entity.getHp();
				entity.setHp(entity.getHp() - getHp());
				setHp(getHp() - eHp);
				break;
			}
		}

		if (getBounds().intersect(getParent().getSafeArea())) {
			if (bullets > 0 && cooldown.use()) {
				AssetManager.playCue("enemy_fire");
				for (int i = 0; i < bullets; i++) {
					double step = 45d / (bullets + 1);
					parent.spawn(new EnemyBullet(this, 1, 180 + -45 / 2d + step * (i + 1)));
				}
			}
		}
	}

	public boolean hit(Entity other) {
		if (!getBounds().intersect(getParent().getSafeArea())) return false;

		return other instanceof Ship && getBounds().intersect(other.getBounds());
	}
}
