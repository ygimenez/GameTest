package com.kuuhaku.entities;

import com.kuuhaku.Game;

public class Boss extends Enemy {
	private boolean left = false;

	public Boss(Game parent) {
		super(parent, "boss.png", 2000, 5, 3);
	}

	@Override
	public void move() {
		getBounds().translate(left ? -0.1 : 0.1, 0);

		if (getX() == 0 || getX() == (getParent().getSafeArea().width - getWidth())) {
			left = !left;
			getBounds().translate(0, 5);
		}
	}
}
