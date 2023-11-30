package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;
import com.kuuhaku.utils.Utils;
import com.kuuhaku.view.GameRuntime;

import java.awt.*;

@Managed
public class Target extends Enemy {
	public Target(GameRuntime runtime) {
		super(runtime, "invader", Integer.MAX_VALUE, 1, 0);

		Rectangle safe = runtime.getSafeArea();
		getBounds().setPosition(Utils.rng().nextInt(safe.width), Utils.rng().nextInt(safe.height));
	}

	@Override
	public void move() {
	}
}
