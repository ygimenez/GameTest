package com.kuuhaku.entities;

public class FastshotPickup extends Pickup {
	public FastshotPickup(Entity owner) {
		super(owner, "fastshot.png");
	}

	@Override
	public void addBonus(Ship ship) {
		ship.setFireRate(ship.getFireRate() + 0.5);
	}
}
