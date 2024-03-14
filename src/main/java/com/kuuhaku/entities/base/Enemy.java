package com.kuuhaku.entities.base;

import com.kuuhaku.entities.projectiles.EnemyProjectile;
import com.kuuhaku.interfaces.*;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Enemy extends Entity implements IDynamic, ICollide, ITrackable, IDamageable {
	private static final List<Class<Pickup>> drops = new ArrayList<>();
	private final boolean spawnDrop = Utils.rng().nextFloat() > 1 - Math.min(getCost() * 0.001f, 0.2f);
	private final Cooldown cooldown;
	private int hp, baseHp;

	static {
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.pickups")) {
			drops.add((Class<Pickup>) klass);
		}
	}

	public Enemy(GameRuntime runtime, Entity parent, int cooldown) {
		super(runtime, parent);
		this.cooldown = new Cooldown(runtime, (int) (cooldown / (1 + 0.1f * runtime.getLevel())));

		Metadata info = getClass().getDeclaredAnnotation(Metadata.class);
		this.hp = this.baseHp = info.hp() * runtime.getLevel();

		if (runtime.getTick() > 0) {
			getSprite().setColor(spawnDrop ? Color.ORANGE.brighter() : runtime.getForeground());
		}

		getCoordinates().setPosition(
				getWidth() / 2f + Utils.rng().nextFloat(getRuntime().getSafeArea().width * 0.8f - getWidth() / 2f),
				getRuntime().getBounds().y + 50
		);
	}

	public final int getCost() {
		return getHp() / 2 + 50 * getRuntime().getLevel();
	}

	public Cooldown getCooldown() {
		return cooldown;
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
		return hp;
	}

	@Override
	public void setHp(int hp) {
		this.hp = hp;
		this.baseHp = Math.max(this.hp, this.baseHp);
	}

	public float getDamageMult() {
		return 1 + 0.3f * (getRuntime().getLevel() - 1);
	}

	public float getSpeedMult() {
		return 1 + 0.2f * (getRuntime().getLevel() - 1);
	}

	@Override
	public void damage(int value) {
		if (!isVisible() || hp <= 0) return;

		this.hp = Utils.clamp(hp - value, 0, baseHp);
		if (hp <= 0) {
			AssetManager.playCue("explode");
			getRuntime().addScore(getCost());

			if (spawnDrop) {
				Class<Pickup> drop = drops.get(ThreadLocalRandom.current().nextInt(drops.size()));

				try {
					getRuntime().spawn(drop.getConstructor(Entity.class).newInstance(this));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		if (wasSpawned()) {
			getRuntime().getSpawnLimit().release();
		}
	}

	@Override
	public boolean hit(Entity other) {
		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}

	public void update() {
		move();
		if (isVisible() && !isCullable()) {
			cooldown.spend();
		}

		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof Enemy) continue;

			if (entity instanceof IDamageable d && hit(entity)) {
				int eHp = d.getHp();
				d.damage(getHp());
				damage(eHp);
				break;
			}
		}

		if (isVisible() && cooldown.use()) {
			shoot();
		}
	}

	abstract protected void move();

	protected void shoot() {
		AssetManager.playCue("enemy_fire");
		getRuntime().spawn(new EnemyProjectile(this, 1, 0));
	}

	public void translate(float dx, float dy) {
		getCoordinates().translate(dx * getSpeedMult(), dy * getSpeedMult());
	}
}
