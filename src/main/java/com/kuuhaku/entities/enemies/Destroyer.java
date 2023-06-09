package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.entities.projectiles.DestroyerTorpedo;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Cooldown;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Managed
public class Destroyer extends Enemy {
	private final int baseHp;
	private final Cooldown primary, secondary, torpedo;
	private boolean inPlace, left, enraged, alternate;
	private int torpLimit = 1;

	public Destroyer(GameRuntime parent) {
		super(parent, "boss_1", (int) (2000 * (1 + parent.getRound() / 10) + parent.getTick() / 20), 12, 3);
		this.baseHp = getHp();
		this.primary = new Cooldown(parent, 2500 / getFireRate());
		this.secondary = new Cooldown(parent, 250 / getFireRate());
		this.torpedo = new Cooldown(parent, 5000 / getFireRate());
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

			inPlace = true;
		}
	}

	@Override
	public void attack() {
		if (inPlace) {
			if (torpLimit > 0 && torpedo.use()) {
				getParent().spawn(new DestroyerTorpedo(this, 0.2));
				torpLimit--;
			}

			if (enraged) {
				if (primary.use()) {
					AssetManager.playCue("enemy_fire");

					if (alternate) {
						getParent().spawn(new EnemyBullet(this, 1, 180));
					} else {
						getParent().spawn(
								new EnemyBullet(this, 1, 180 - 30),
								new EnemyBullet(this, 1, 180 + 30)
						);
					}

					alternate = !alternate;
				}

				if (secondary.use()) {
					getParent().spawn(
							new EnemyBullet(this, 2, 180 - 45),
							new EnemyBullet(this, 2, 180 + 45)
					);
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
		if (getY() < getParent().getSafeArea().height / 20) return;

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
			torpLimit++;
		}
	}

	public int getTorpLimit() {
		return torpLimit;
	}

	public void setTorpLimit(int torpLimit) {
		this.torpLimit = torpLimit;
	}
}
