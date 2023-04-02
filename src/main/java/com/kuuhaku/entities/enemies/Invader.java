package com.kuuhaku.entities.enemies;

import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;

import java.awt.*;

@Managed
public class Invader extends Enemy {
	private int drift;
	private long lastDrift;
	private double speedRoll = 1 + Math.random() / 5;

	public Invader(GameRuntime parent) {
		super(parent, "invader", 100, 3, 1);
	}

	@Override
	public void move() {
		Rectangle safe = getParent().getSafeArea();
		if (getY() > safe.height / 10 && Math.random() > 0.995 && drift == 0) {
			drift = Math.random() > 0.5 ? 1 : -1;
			lastDrift = getParent().getTick();
		}

		if ((drift < 0 && getX() <= safe.width / 4) || (drift > 0 && getX() >= safe.width / 4 * 3)) {
			drift = 0;
		}

		if (drift != 0) {
			getBounds().translate(0.3 * speedRoll * drift, 0);

			if (getParent().getTick() - lastDrift > 100) {
				drift = 0;
			}
		} else {
			getBounds().translate(0, 0.3 * speedRoll);
		}
	}
}
