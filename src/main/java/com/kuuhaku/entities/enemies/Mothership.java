package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Boss;
import com.kuuhaku.entities.other.MothershipBomb;
import com.kuuhaku.entities.other.MothershipLaser;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Managed
@Metadata(sprite = "boss_2", hp = 3000)
public class Mothership extends Boss {
	private boolean spawned;
	private int angle = 0;

	private boolean once = false;

	private final List<Runnable> rotation = new ArrayList<>();

	public Mothership(GameRuntime runtime) {
		super(runtime, null, 5000);
		getCooldown().pause();
	}

	@Override
	public void move() {
		float[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();

		if (pos[1] < safe.height / 20f) {
			getCoordinates().translate(0, 0.1f);
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

		getCoordinates().translate(Utils.round((safe.width / 2f - pos[0] - getWidth() / 2f) / 1000f, 2), 0);
	}

	@Override
	public void shoot() {
		getCooldown().pause();

		if (rotation.isEmpty()) {
			rotation.addAll(List.of(
//					this::laserSweep,
					this::artillery
			));

			Collections.shuffle(rotation);
		}

		CompletableFuture.runAsync(() -> {
			rotation.remove(0).run();
			getCooldown().resume();
		});
	}

	private void laserSweep() {
			getRuntime().spawn(new MothershipLaser(this));
			once = true;
	}

	private void artillery() {
		getRuntime().spawn(new MothershipBomb(this));
		once = true;
	}

	@Override
	public void setHp(int hp) {
		float[] pos = getPosition();
		if (pos[1] < getRuntime().getSafeArea().height / 20f) return;
		super.setHp(hp);
	}

	@Override
	protected void onEnrage() {
		getCooldown().setTime((int) (getCooldown().getTime() / 1.5f));
	}
}
