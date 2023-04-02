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

	public Snake(GameRuntime parent) {
		super(parent, "snake", 25, 4, 1);
	}

	@Override
	public void move() {
		if (getBounds().intersect(getParent().getSafeArea())) {
			getBounds().translate(Math.sin(Math.toRadians(angle++ / 2d)), 0.3 + Math.cos(Math.toRadians(angle / 2d)) * 0.6);

			if (!spawned && segments > 0) {
				angle = 0;

				Snake seg = new Snake(getParent());
				seg.segments = segments - 1;
				getParent().spawn(seg);
				seg.getBounds().setPosition(getX(), getY() - getHeight() / 2d);
				spawned = true;
			}
		} else {
			getBounds().translate(0, 0.3);
		}
	}
}
