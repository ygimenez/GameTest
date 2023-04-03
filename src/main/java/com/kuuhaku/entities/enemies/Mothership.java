package com.kuuhaku.entities.enemies;

import com.kuuhaku.AssetManager;
import com.kuuhaku.Cooldown;
import com.kuuhaku.Utils;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.pickups.HealthPickup;
import com.kuuhaku.entities.projectiles.EnemyBullet;
import com.kuuhaku.entities.projectiles.MothershipBarrage;
import com.kuuhaku.entities.projectiles.MothershipLaser;
import com.kuuhaku.interfaces.IProjectile;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.view.GameRuntime;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Managed
public class Mothership extends Enemy {
	private final int baseHp;
	private final Cooldown primary, attack;
	private boolean spawned, inPlace, resumed, enraged;
	private int angle = 0;

	private final List<Runnable> rotation = new ArrayList<>();

	public Mothership(GameRuntime parent) {
		super(parent, "boss_2", (int) (3000 * (1 + parent.getRound() / 20) + parent.getTick() / 20), 5, 0);
		this.baseHp = getHp();
		this.primary = new Cooldown(parent, 2500 / getFireRate());
		this.attack = new Cooldown(parent, 1000);

		attack.pause();
	}

	@Override
	public void move() {
		Rectangle safe = getParent().getSafeArea();

		if (getY() < safe.height / 20) {
			getBounds().translate(0, 0.1);
		} else if (!spawned) {
			CompletableFuture.runAsync(() -> {
				for (int i = 0; i < 10; i++) {
					getParent().spawn(new Defender(this));
					Utils.await(getParent(), 36);
				}

				inPlace = true;
			});

			spawned = true;
		}

		getBounds().translate(
				BigDecimal.valueOf((safe.width / 2d - getX() - getWidth() / 2d) / 1000d)
						.setScale(2, RoundingMode.HALF_EVEN)
						.doubleValue(),
				0
		);
	}


	@Override
	public void update() {
		move();

		if (inPlace && !resumed) {
			attack.resume();
			resumed = true;
		}

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
			if (enraged && primary.use()) {
				Entity player = getParent().getPlayer();
				double dx = (player.getX() + player.getWidth() / 2d) - (getX() + getWidth() / 2d);
				double dy = (player.getY() + player.getHeight() / 2d) - (getY() + getHeight() / 2d);

				AssetManager.playCue("enemy_fire");
				getParent().spawn(new EnemyBullet(this, 1, 90 + Math.toDegrees(Math.atan2(dy, dx))));
			}

			if (attack.use()) {
				attack.pause();

				if (rotation.isEmpty()) {
					rotation.addAll(List.of(
							this::laserBeam,
							this::bulletHell,
							this::barrage,
							this::shotgun
					));

					Collections.shuffle(rotation);
				}

				CompletableFuture.runAsync(() -> {
					rotation.remove(0).run();
					attack.resume();
				});
			}
		}
	}

	private void laserBeam() {
		Clip cue = AssetManager.getAudio("mothership_laser");
		if (cue != null) {
			cue.loop(Clip.LOOP_CONTINUOUSLY);
		}

		angle = 0;
		if (enraged) {
			for (int i = 0; i < 180; i++, angle++) {
				if (getHp() == 0) return;

				double cos = Math.cos(Math.toRadians(angle));
				getParent().spawn(
						new MothershipLaser(this, 90 - 75 * cos),
						new MothershipLaser(this, 270 + 75 * cos)
				);
				Utils.await(getParent(), 2);
			}
		} else {
			for (int i = 0; i < 180; i++, angle++) {
				if (getHp() == 0) return;

				getParent().spawn(new MothershipLaser(this, 180 + 45 * Math.cos(Math.toRadians(angle))));
				Utils.await(getParent(), 2);
			}
		}

		if (cue != null) {
			cue.stop();
		}
	}

	private void bulletHell() {
		angle = 0;
		for (int i = 0; i < 100; i++, angle++) {
			if (getHp() == 0) return;

			AssetManager.playCue("enemy_fire");
			getParent().spawn(
					new EnemyBullet(this, 0.5, angle + angle * 10),
					new EnemyBullet(this, 0.5, angle + 90 + angle * 10),
					new EnemyBullet(this, 0.5, angle + 180 + angle * 10),
					new EnemyBullet(this, 0.5, angle + 270 + angle * 10)
			);
			Utils.await(getParent(), enraged ? 30 : 50);
		}
	}

	private void barrage() {
		Rectangle safe = getParent().getSafeArea();
		for (int i = 0; i < (enraged ? 15 : 10); i++, angle++) {
			if (getHp() == 0) return;

			int offset = ThreadLocalRandom.current().nextInt(safe.height / 3 * 2);
			boolean left = ThreadLocalRandom.current().nextBoolean();

			AssetManager.playCue("enemy_fire");
			for (int j = 0; j < 8; j++) {
				getParent().spawn(new MothershipBarrage(this, left, offset + j * 25, left ? 90 : 270));
			}

			Utils.await(getParent(), enraged ? 150 : 200);
		}
	}

	private void shotgun() {
		Entity player = getParent().getPlayer();
		for (int i = 0; i < (enraged ? 3 : 2); i++) {
			if (getHp() == 0) return;

			double dx = (player.getX() + player.getWidth() / 2d) - (getX() + getWidth() / 2d);
			double dy = (player.getY() + player.getHeight() / 2d) - (getY() + getHeight() / 2d);

			AssetManager.playCue("enemy_fire");
			for (int j = 0; j < 5; j++) {
				double step = 20d / (5 + 1);
				getParent().spawn(new EnemyBullet(this, 1, 90 + Math.toDegrees(Math.atan2(dy, dx)) + -20 / 2d + step * (j + 1)));
			}

			Utils.await(getParent(), enraged ? 50 : 100);
		}
	}

	@Override
	public void setHp(int hp) {
		if (getY() < getParent().getSafeArea().height / 20) return;

		super.setHp(hp);
		if (getHp() <= 0) {
			AssetManager.playCue("boss_explode");

			CompletableFuture.runAsync(
					() -> AssetManager.playCue("boss_win"),
					CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
			);
		} else if (getHp() < baseHp / 2 && !enraged) {
			attack.setTime(2000);

			getParent().spawn(new HealthPickup(this));
			getParent().spawn(new HealthPickup(this));
			enraged = true;
		}
	}
}
