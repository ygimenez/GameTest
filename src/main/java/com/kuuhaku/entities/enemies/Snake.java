package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.interfaces.Metadata;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.util.concurrent.ThreadLocalRandom;

@Managed
@Metadata(sprite = "snake", hp = 25)
public class Snake extends Enemy {
	private int angle;
	private int segments = ThreadLocalRandom.current().nextInt(2, 8);
	private boolean spawned;

	public Snake(GameRuntime runtime) {
		super(runtime, null, 2000);
	}

	@Override
	public void move() {
		if (getCoordinates().intersect(getRuntime().getSafeArea())) {
			getCoordinates().translate(Utils.fsin((float) Math.toRadians(angle++ / 2f)), 0.3f + Utils.fcos((float) Math.toRadians(angle / 2f)) * 0.6f);

			if (!spawned && segments > 0) {
				angle = 0;

				Snake seg = new Snake(getRuntime());
				seg.segments = segments - 1;
				seg.getCooldown().setOffset(seg.segments * 50);
				getRuntime().spawn(seg);

				float[] pos = getPosition();
				seg.getCoordinates().setPosition(pos[0], pos[1] - getHeight() / 2f);
				spawned = true;
			}
		} else {
			getCoordinates().translate(0, 0.3f);
		}
	}
}
