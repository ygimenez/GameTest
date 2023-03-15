package com.kuuhaku.entities;

public class SpeedPickup extends Pickup {
	public SpeedPickup(Entity owner) {
		super(owner, "speed.png");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setSpeed(ship.getSpeed() + 0.1);
	}
}
