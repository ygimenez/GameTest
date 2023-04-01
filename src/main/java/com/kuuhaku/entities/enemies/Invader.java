package com.kuuhaku.entities.enemies;

import com.kuuhaku.GameRuntime;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;

@Managed
public class Invader extends Enemy {
	public Invader(GameRuntime parent) {
		super(parent, "invader", 100, 3, 1);
	}

	@Override
	public void move() {
		getBounds().translate(0, 0.3);
	}
}
