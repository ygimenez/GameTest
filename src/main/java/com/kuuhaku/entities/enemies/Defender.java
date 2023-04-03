package com.kuuhaku.entities.enemies;

import com.kuuhaku.entities.base.Enemy;

public class Defender extends Enemy {
	private final Mothership owner;
	private int angle, radius;

	public Defender(Mothership owner) {
		super(owner.getParent(), "snake", 250, 1, 0);
		this.owner = owner;

		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d
		);
	}

	@Override
	public void move() {
		getBounds().setPosition(
				owner.getX() + owner.getWidth() / 2d - getWidth() / 2d + Math.sin(Math.toRadians(angle)) * radius / 2d,
				owner.getY() + owner.getHeight() / 2d - getHeight() / 2d + Math.cos(Math.toRadians(angle)) * radius / 2d
		);

		if (radius < 180) {
			radius++;
		} else {
			angle++;
		}
	}
}
