package com.kuuhaku.entities;

import com.kuuhaku.Game;

public class Waver extends Enemy {
	private int angle;

	public Waver(Game parent) {
		super(parent, "waver.png", 150, 5, 1);
	}

	@Override
	public void move() {
		getBounds().translate(Math.sin(Math.toRadians(angle++)) * 2, 0.3);
	}

	@Override
	public void destroy() {
		getParent().getWavLimit().release();
	}
}
