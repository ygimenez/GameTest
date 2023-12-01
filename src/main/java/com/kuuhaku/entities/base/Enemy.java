package com.kuuhaku.entities.base;

import com.kuuhaku.entities.Player;
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
	private final Cooldown cooldown;
	private final boolean spawnDrop = Utils.rng().nextFloat() > 1 - (getCost() * 0.0002f);
	private int hp, baseHp;

	static {
		for (Class<?> klass : Utils.getAnnotatedClasses(Managed.class, "com.kuuhaku.entities.pickups")) {
			drops.add((Class<Pickup>) klass);
		}
	}

	public Enemy(GameRuntime runtime, Entity parent, int cooldown) {
		super(runtime, parent);
		this.cooldown = new Cooldown(runtime, cooldown);

		Metadata info = getClass().getDeclaredAnnotation(Metadata.class);
		this.hp = this.baseHp = (int) (info.hp() * (runtime.getRound() / 5f));

		if (runtime.getTick() > 0) {
			getSprite().setColor(spawnDrop ? Color.ORANGE.brighter() : runtime.getForeground());
		}

		getCoordinates().setPosition(
				Utils.rng().nextFloat(getRuntime().getSafeArea().width * 0.8f - getWidth()),
				getRuntime().getBounds().y + 50
		);
	}

	public final int getCost() {
		return getHp() / 2 + 50 * getRuntime().getRound();
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
		this.hp = Utils.clamp(hp, 0, baseHp);
		if (hp <= 0) {
			AssetManager.playCue("explode");

			getRuntime().addScore(getCost());

			if (spawnDrop) {
				Class<Pickup> drop = drops.get(ThreadLocalRandom.current().nextInt(drops.size()));

				try {
					getRuntime().spawn(drop.getConstructor(Entity.class).newInstance(this));
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException |
						 NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		getRuntime().getSpawnLimit().release();
	}

	@Override
	public boolean hit(Entity other) {
		if (other instanceof IParticle) return false;
		else if (!isVisible()) return false;

		return other instanceof Player && getCoordinates().intersect(other.getCoordinates());
	}

	public void update() {
		move();
		if (isVisible() && !isCullable()) {
			cooldown.spend();
		}

		for (Entity entity : getRuntime().getEntities()) {
			if (entity instanceof Enemy) continue;
			else if (entity instanceof IDamageable d && hit(entity)) {
				int eHp = d.getHp();
				d.setHp(d.getHp() - getHp());
				setHp(getHp() - eHp);
				break;
			}
		}

		if (isVisible() && cooldown.use()) {
			shoot();
		}
	}

	abstract public void move();

	public void shoot() {
		AssetManager.playCue("enemy_fire");
		getRuntime().spawn(new EnemyProjectile(this, 1, 0));
	}
}
