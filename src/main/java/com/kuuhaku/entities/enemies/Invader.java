package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

@Managed
public class Invader extends Enemy {
	private int drift;
	private long lastDrift;
	private double speedRoll = 1 + Utils.rng().nextDouble(0.3);

	public Invader(GameRuntime runtime) {
		super(runtime, "invader", 100, 4, 1);
	}

	@Override
	public void move() {
		double[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();

		if (pos[1] > safe.height / 10d && Utils.rng().nextDouble() > 0.995 && drift == 0) {
			drift = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
			lastDrift = getRuntime().getTick();
		}

		if ((drift < 0 && pos[0] <= safe.width / 4d) || (drift > 0 && pos[0] >= safe.width / 4d * 3)) {
			drift = 0;
		}

		if (drift != 0) {
			getBounds().translate(0.3 * speedRoll * drift, 0);

			if (getRuntime().getTick() - lastDrift > 100) {
				drift = 0;
			}
		} else {
			getBounds().translate(0, 0.3 * speedRoll);
		}
	}
}
