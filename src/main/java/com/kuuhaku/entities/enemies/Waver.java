package com.kuuhaku.entities.enemies;

import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;

@Managed
public class Waver extends Enemy {
	private int angle;

	public Waver(GameRuntime parent) {
		super(parent, "waver", 150, 5, 1);
	}

	@Override
	public void move() {
		getBounds().translate(Math.sin(Math.toRadians(angle++ / 2d)) * 2, 0.3);
	}
}
