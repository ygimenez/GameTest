package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.view.GameRuntime;

import java.util.concurrent.ThreadLocalRandom;

@Managed
public class Snake extends Enemy {
	private int angle;
	private int segments = ThreadLocalRandom.current().nextInt(4, 8);
	private boolean spawned;

	public Snake(GameRuntime runtime) {
		super(runtime, "snake", 25, 4, 1);
	}

	@Override
	public void move() {
		if (getBounds().intersect(getRuntime().getSafeArea())) {
			getBounds().translate(Math.sin(Math.toRadians(angle++ / 2d)), 0.3 + Math.cos(Math.toRadians(angle / 2d)) * 0.6);

			if (!spawned && segments > 0) {
				angle = 0;

				Snake seg = new Snake(getRuntime());
				seg.segments = segments - 1;
				getRuntime().spawn(seg);

				double[] pos = getPosition();
				seg.getBounds().setPosition(pos[0], pos[1] - getHeight() / 2d);
				spawned = true;
			}
		} else {
			getBounds().translate(0, 0.3);
		}
	}
}
