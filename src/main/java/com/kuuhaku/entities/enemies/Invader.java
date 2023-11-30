package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

@Managed
@Metadata(sprite = "invader", hp = 100)
public class Invader extends Enemy {
	private final float speedRoll = 1 + Utils.rng().nextFloat(0.3f);
	private int drift;
	private long lastDrift;

	public Invader(GameRuntime runtime) {
		super(runtime, null, 2500);
	}

	@Override
	public void move() {
		float[] pos = getPosition();
		Rectangle safe = getRuntime().getSafeArea();

		if (pos[1] > safe.height / 10f && Utils.rng().nextFloat() > 0.995f && drift == 0) {
			drift = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
			lastDrift = getRuntime().getTick();
		}

		if ((drift < 0 && pos[0] <= safe.width / 4f) || (drift > 0 && pos[0] >= safe.width / 4f * 3)) {
			drift = 0;
		}

		if (drift != 0) {
			getCoordinates().translate(0.3f * speedRoll * drift, 0);

			if (getRuntime().getTick() - lastDrift > 100) {
				drift = 0;
			}
		} else {
			getCoordinates().translate(0, 0.3f * speedRoll);
		}
	}
}
