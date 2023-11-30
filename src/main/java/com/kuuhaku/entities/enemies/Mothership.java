package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Boss;
import com.kuuhaku.entities.projectiles.MothershipLaser;
import com.kuuhaku.entities.projectiles.TrackableBullet;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.manager.AssetManager;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Managed
public class Mothership extends Boss {
	private boolean spawned;
	private int angle = 0;

	private boolean once = false;

	private final List<Runnable> rotation = new ArrayList<>();

	public Mothership(GameRuntime runtime) {
		super(runtime, "boss_2", (int) (3000 * (1 + runtime.getRound() / 20) + runtime.getTick() / 20), 2, 0);
		getCooldown().pause();
	}

	@Override
	public void move() {
		double[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();

		if (pos[1] < safe.height / 20d) {
			getBounds().translate(0, 0.1);
		} else if (!spawned) {
			CompletableFuture.runAsync(() -> {
				for (int i = 0; i < 1; i++) {
					getRuntime().spawn(new Defender(this));
					Utils.await(getRuntime(), 36);
				}

				getCooldown().resume();
			});

			spawned = true;
		}

		getBounds().translate(Utils.round((safe.width / 2d - pos[0] - getWidth() / 2d) / 1000d, 2), 0);
	}

	@Override
	public void attack() {
		if (getCooldown().use()) {
			getCooldown().pause();

			if (rotation.isEmpty()) {
				rotation.addAll(List.of(
//							this::bulletHell,
//						this::barrage,
						this::laserTest
				));

				Collections.shuffle(rotation);
			}

			CompletableFuture.runAsync(() -> {
				rotation.remove(0).run();
				getCooldown().resume();
			});
		}
	}

	private void bulletHell() {
		angle = 0;
		for (int i = 0; i < 100; i++, angle++) {
			if (getHp() == 0) return;

			AssetManager.playCue("enemy_fire");
			for (int j = 0; j < 4; j++) {
//				getParent().spawn(new EnemyAccelBullet(this, 0, angle * 1.5 + 90 * j + angle * 10));
			}

			Utils.await(getRuntime(), isEnraged() ? 15 : 25);
		}
	}

	private void laserTest() {
		if (!once) {
			getRuntime().spawn(new MothershipLaser(this));
			once = true;
		}
	}

	private void barrage() {
		Rectangle safe = getRuntime().getSafeArea();
		for (int i = 0; i < (isEnraged() ? 15 : 10); i++, angle++) {
			if (getHp() == 0) return;

			int bullets = safe.height / 100;
			int offset = ThreadLocalRandom.current().nextInt(safe.height / 3 * 2);
			boolean left = ThreadLocalRandom.current().nextBoolean();

			AssetManager.playCue("enemy_fire");
			for (int j = 0; j < bullets; j++) {
				getRuntime().spawn(new TrackableBullet(this, 1.5, left ? 90 : 270, new Point(
						left ? safe.x - 100 : safe.x + safe.width + 100, offset + j * 30
				)));
				Utils.await(getRuntime(), 10);
			}

			Utils.await(getRuntime(), isEnraged() ? 120 : 200);
		}
	}

	@Override
	public void setHp(int hp) {
		double[] pos = getPosition();
		if (pos[1] < getRuntime().getSafeArea().height / 20d) return;
		super.setHp(hp);
	}

	@Override
	protected void onEnrage() {
		getCooldown().setTime((int) (getCooldown().getTime() / 1.5));
	}
}
