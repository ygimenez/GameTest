package com.kuuhaku.entities;

import com.kuuhaku.Game;

public class Invader extends Enemy {
	public Invader(Game parent) {
		super(parent, "invader.png", 100, 3, 1);
	}

	@Override
	public void move() {
		getBounds().translate(0, 0.3);
	}
}
