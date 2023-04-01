package com.kuuhaku.entities.enemies;

import com.kuuhaku.view.GameRuntime;
import com.kuuhaku.entities.base.Enemy;
import com.kuuhaku.interfaces.Managed;

import java.awt.*;

@Managed
public class Boss extends Enemy {
	private boolean left = false;

	public Boss(GameRuntime parent) {
		super(parent, "boss", 2000, 6, 3);
	}

	@Override
	public void move() {
		getBounds().translate(left ? -1 : 1, 0);

		Rectangle safe = getParent().getSafeArea();
		if ((left && getX() <= safe.width / 4) || (!left && getX() >= safe.width / 4 * 3)) {
			left = !left;
			getBounds().translate(0, 10);
		}
	}
}
