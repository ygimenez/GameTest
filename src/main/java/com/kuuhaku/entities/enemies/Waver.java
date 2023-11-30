package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

@Managed
public class Waver extends Enemy {
	private int angle;

	public Waver(GameRuntime runtime) {
		super(runtime, "waver", 150, 6, 2);
	}

	@Override
	public void move() {
		getBounds().translate(Utils.fsin(Math.toRadians(angle++ / 2d)), 0.3);
	}
}
