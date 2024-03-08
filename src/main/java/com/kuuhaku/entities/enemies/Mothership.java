package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.Player;
import com.kuuhaku.entities.base.Boss;
import com.kuuhaku.entities.base.Entity;
import com.kuuhaku.entities.other.MothershipBomb;
import com.kuuhaku.entities.other.MothershipLaserShot;
import com.kuuhaku.entities.other.MothershipLaserSweep;
import com.kuuhaku.entities.projectiles.MeteorProjectile;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Managed
@Metadata(sprite = "boss_2", hp = 3000)
public class Mothership extends Boss {
	private final List<Runnable> rotation = new ArrayList<>();
	private boolean spawned;

	public Mothership(GameRuntime runtime) {
		super(runtime, null, 7000);
		getCooldown().pause();
	}

	@Override
	public void move() {
		float[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();

		if (pos[1] < safe.height / 20f) {
			translate(0, 0.15f);
		} else if (!spawned) {
			CompletableFuture.runAsync(() -> {
				for (int i = 0; i < 10; i++) {
					getRuntime().spawn(new Defender(this));
					Utils.await(getRuntime(), 36);
				}

				getCooldown().resume();
			});

			spawned = true;
		}

		translate(Utils.round((safe.width / 2f - pos[0]) / 1000f, 2), 0);
	}

	@Override
	public void shoot() {
		getCooldown().pause();

		if (rotation.isEmpty()) {
			rotation.addAll(List.of(
					this::laserSweep,
					this::artillery,
					this::laserBarrage,
					this::laserCross,
					this::meteorMaze
			));

			Collections.shuffle(rotation);
		}

		CompletableFuture.runAsync(() -> {
			rotation.remove(0).run();
			getCooldown().resume();
		});
	}

	private void laserSweep() {
		getRuntime().spawn(new MothershipLaserSweep(this));
	}

	private void artillery() {
		for (int i = 0; i < 10; i++) {
			getRuntime().spawn(new MothershipBomb(this));
			Utils.await(getRuntime(), getCooldown().getTime() / 5);
		}
	}

	private void laserBarrage() {
		Rectangle safe = getRuntime().getSafeArea();
		boolean reverse = Utils.rng().nextBoolean();

		for (int i = 0; i < 10; i++) {
			getRuntime().spawn(new MothershipLaserShot(this,
					safe.width / 2,
					reverse ? safe.height - safe.height / 15 * i : safe.height / 15 * i,
					90
			));
			Utils.await(getRuntime(), getCooldown().getTime() / 20);
		}
	}

	private void laserCross() {
		Rectangle safe = getRuntime().getSafeArea();
		Player target = getRuntime().getRandomPlayer();

		for (int i = 0; i < 2; i++) {
			Entity laser = new MothershipLaserShot(this);
			laser.getCoordinates().setAnchor(0.5f, 0);
			laser.getCoordinates().setPosition(safe.width * i - (5 - 10 * i), -5);
			laser.getCoordinates().setAngle((float) Math.toRadians(Utils.angBetween(
					new Point2D.Float(safe.width * i - (5 - 10 * i), -5), target.getGlobalCenter())
			));

			getRuntime().spawn(laser);
		}
	}

	private void meteorMaze() {
		Rectangle safe = getRuntime().getSafeArea();

		Entity ref = new MeteorProjectile(this, 0, 0, 0);
		int radius = (int) (Math.max(ref.getWidth(), ref.getHeight()) * 1.25f);

		int last = -1;
		for (int wave = 0; wave < 5; wave++) {
			int direction = -1;
			while (direction == last || direction == -1) {
				direction = Utils.rng().nextInt(4);
			}

			last = direction;
			int origin = direction % 2;
			int count = direction < 2 ? safe.width / radius : safe.height / radius;
			int holes = 0;

			for (int i = 0; i < count; i++) {
				boolean hole = (holes < 3 && Utils.rng().nextBoolean()) || (holes == 0 && i == count - 1);

				if (hole) holes++;
				else {
					if (direction < 2) {
						getRuntime().spawn(new MeteorProjectile(this,
								radius * i,
								safe.height * origin - (origin == 0 ? radius : -radius),
								direction
						));
					} else {
						getRuntime().spawn(new MeteorProjectile(this,
								safe.width * origin - (origin == 0 ? radius : -radius),
								radius * i,
								direction
						));
					}
				}
			}

			Utils.await(getRuntime(), getCooldown().getTime() / 3);
		}
	}

	@Override
	public void damage(int value) {
		float[] pos = getPosition();
		if (pos[1] < getRuntime().getSafeArea().height / 20f) return;
		super.damage(value);
	}

	@Override
	public boolean isEnraged() {
		return super.isEnraged() || getChildren().isEmpty();
	}

	@Override
	protected void onEnrage() {
		getCooldown().setTime((int) (getCooldown().getTime() / 1.25f));
	}
}
