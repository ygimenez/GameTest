package com.kuuhaku.entities.enemies;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Cooldown;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Managed
public class Boss extends Enemy {
	private final int baseHp;
	private final Cooldown primary, secondary;
	private boolean left, enraged, alternate;
	private int angle = 0;

	public Boss(GameRuntime parent) {
		super(parent, "boss", 2000, 12, 3);
		this.baseHp = getHp();
		this.primary = new Cooldown(parent, 2500 / getFireRate());
		this.secondary = new Cooldown(parent, 250 / getFireRate());
	}

	@Override
	public void move() {
		Rectangle safe = getParent().getSafeArea();

		if (getY() < safe.height / 20) {
			getBounds().translate(0, 0.1);
		} else {
			getBounds().translate(left ? -0.5 : 0.5, 0);

			if ((left && getX() <= safe.width / 4) || (!left && getX() >= safe.width / 4 * 3)) {
				left = !left;
			}
		}
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
			if (enraged) {
				if (primary.use()) {
					AssetManager.playCue("enemy_fire");

					if (alternate) {
						getParent().spawn(new EnemyBullet(this, 1, 180));
					} else {
						getParent().spawn(new EnemyBullet(this, 1, 180 - 30));
						getParent().spawn(new EnemyBullet(this, 1, 180 + 30));
					}

					alternate = !alternate;
				}

				if (secondary.use()) {
					getParent().spawn(new EnemyBullet(this, 2, 180 - 45));
					getParent().spawn(new EnemyBullet(this, 2, 180 + 45));
				}
			} else {
				if (primary.use()) {
					AssetManager.playCue("enemy_fire");
					for (int i = 0; i < getBullets(); i++) {
						double step = 45d / (getBullets() + 1);
						getParent().spawn(new EnemyBullet(this, 1, 180 + -45 / 2d + step * (i + 1)));
					}
				}
			}
		}
	}

	@Override
	public void setHp(int hp) {
		super.setHp(hp);
		if (getHp() <= 0) {
			AssetManager.playCue("boss_explode");

			for (int i = 0; i < 360; i += 10) {
				getParent().spawn(new EnemyBullet(this, 1, i));
			}

			CompletableFuture.runAsync(
					() -> AssetManager.playCue("boss_win"),
					CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
			);
		} else if (getHp() < baseHp / 2 && !enraged) {
			primary.setTime(1000 / getFireRate());
			getParent().spawn(new HealthPickup(this));
			getParent().spawn(new HealthPickup(this));
			enraged = true;
		}
	}
}
