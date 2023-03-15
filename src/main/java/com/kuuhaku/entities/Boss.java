package com.kuuhaku.entities;

import com.kuuhaku.Game;

import java.awt.*;

public class Boss extends Enemy {
	private boolean left = false;

	public Boss(Game parent) {
		super(parent, "boss.png", 2000, 6, 3);
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
