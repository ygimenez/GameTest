package com.kuuhaku.entities;

import com.kuuhaku.Game;

import java.awt.*;

public class Boss extends Enemy {
	private boolean left = false;

	public Boss(Game parent) {
		super(parent, "boss.png", 2000, 5, 3);
	}

	@Override
	public void move() {
		getBounds().translate(left ? -2 : 2, 0);

		Rectangle safe = getParent().getSafeArea();
		if ((left && getX() <= safe.width / 3) || (!left && getX() >= safe.width / 3 * 2)) {
			left = !left;
			getBounds().translate(0, 10);
		}
	}
}
